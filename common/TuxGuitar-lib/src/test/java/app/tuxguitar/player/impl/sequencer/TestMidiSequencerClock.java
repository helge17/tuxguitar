package app.tuxguitar.player.impl.sequencer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.player.base.MidiReceiverRecorder;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.thread.TGThreadHandler;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.thread.TGThreadPriority;
import app.tuxguitar.util.TGContext;

/**
 * The sequencer playing on a synthesizer's sample clock: before each block of 512 frames the clock renders, the
 * sequencer sends the events whose ticks sound in the block, each to the clock's receiver of its frame. A tick sounds on
 * the frame its time from where playing started reaches, rounded to the nearest frame; a tempo event starts its tempo on
 * its own frame.
 *
 * The clock runs at 44100 Hz; at 120 bpm (500000 microseconds per quarter) a tick lasts 22.96875 frames. A received
 * call is written as MidiReceiverRecorder writes it; a call the sequencer timed has "@frame " before it, the frame
 * counted from the first block (the receiver of frame 16 of the second block writes "@528").
 */
public class TestMidiSequencerClock {

	private static final int BLOCK = 512;

	private final List<String> received = new ArrayList<String>();
	private final List<Runnable> timers = new ArrayList<Runnable>();
	private final TGContext context = new TGContext();
	private final MidiSequencerImpl sequencer = new MidiSequencerImpl(context);
	private final MidiSequenceHandler sequence;
	private MidiSampleClock.Listener listener;
	// the first frame of the next block
	private long blockStart;
	// how many frames after an event's frame the clock's output sounds it
	private int delay;

	private final MidiTransport transport = new MidiTransport();
	private final MidiSampleClock clock = new MidiSampleClock() {
		public float getSampleRate() {
			return 44100;
		}

		public MidiTransport getTransport() {
			return transport;
		}

		public int getSoundingDelay() {
			return delay;
		}

		public void setListener(Listener listener) {
			TestMidiSequencerClock.this.listener = listener;
		}

		public MidiReceiver at(int frame) {
			return new MidiReceiverRecorder(received, "@" + (blockStart + frame) + " ");
		}
	};

	public TestMidiSequencerClock() throws MidiPlayerException {
		TGThreadManager.getInstance(context).setThreadHandler(new TGThreadHandler() {
			public void start(TGThreadPriority priority, Runnable runnable) {
				timers.add(runnable);
			}

			public void loop(TGThreadLoop loop) {
			}

			public void yield() {
			}

			public void dispose() {
			}

			public Object getThreadId() {
				return null;
			}
		});
		MidiTransmitter transmitter = new MidiTransmitter();
		transmitter.addReceiver("recorder", new MidiReceiverRecorder(received));
		sequencer.setTransmitter(transmitter);
		sequence = sequencer.createSequence(2);
	}

	private void note(long tick, int track, int key) {
		sequence.addNoteOn(tick, track, 1, key, 100, 1, false);
	}

	// with a last note far on, so the sequencer plays on
	private void playOnTheClockFrom(long position) throws MidiPlayerException {
		note(100000, 0, 0);
		playToTheEndFrom(position);
	}

	private void playToTheEndFrom(long position) throws MidiPlayerException {
		sequencer.setClock(clock);
		sequencer.start();
		sequencer.setTickPosition(position);
	}

	// what the sequencer sent before the clock's next blocks
	private String blocks(int count) throws MidiPlayerException {
		received.clear();
		for (int b = 0; b < count; b++) {
			listener.beforeBlock(clock, BLOCK);
			blockStart += BLOCK;
		}
		return String.join(", ", received);
	}

	// the notes on the ticks, keys 60, 61, ... on track 0
	private void notes(long... ticks) {
		for (int i = 0; i < ticks.length; i++) {
			note(ticks[i], 0, 60 + i);
		}
	}

	@Test
	public void eachEventGoesToTheFrameItsTickSoundsOnInItsBlock() throws MidiPlayerException {
		notes(0, 10, 22, 23);
		playOnTheClockFrom(0);

		assertEquals("all notes off, pitch bend reset, @0 on 1 60 100 1 0, @230 on 1 61 100 1 0, @505 on 1 62 100 1 0"
				+ ", @528 on 1 63 100 1 0", blocks(2));
	}

	// the parser adds a track's events, then the next track's
	@Test
	public void eventsAddedOutOfTickOrderGoOutInTickOrder() throws MidiPlayerException {
		note(600, 0, 60);
		note(10, 1, 61);
		playOnTheClockFrom(0);

		assertEquals("all notes off, pitch bend reset, @230 on 1 61 100 1 0, @13781 on 1 60 100 1 0", blocks(27));
	}

	// at 696599 microseconds per quarter tick 16 sounds on frame 512.0003: the first of the second block
	@Test
	public void anEventOnTheFirstFrameOfABlockGoesOutWithThatBlock() throws MidiPlayerException {
		sequence.addTempoInUSQ(0, 0, 696599);
		notes(16);
		playOnTheClockFrom(0);
		String first = blocks(1);

		assertEquals("all notes off, pitch bend reset / @512 on 1 60 100 1 0", first + " / " + blocks(1));
	}

	// 60 bpm from tick 10 (frame 230): tick 20 sounds 459 frames later; 641025 microseconds per quarter (117 bpm at
	// 80%, 93.6 bpm) from tick 20: a quarter lasts 28269.2 frames, not the 28451.6 of a whole 93 bpm
	@Test
	public void aTempoEventStartsItsExactTempoOnItsOwnFrame() throws MidiPlayerException {
		sequence.addTempoInUSQ(10, 0, 1000000);
		sequence.addTempoInUSQ(20, 0, 641025);
		notes(20, 980);

		playOnTheClockFrom(0);

		assertEquals("all notes off, pitch bend reset, @689 on 1 60 100 1 0, @28958 on 1 61 100 1 0", blocks(57));
	}

	// before each block the clock's transport is told the song plays, at the tempo, from the quarter note reached (the
	// song starts at tick 960, its quarter 0): at 60 bpm a quarter lasts 44100 frames
	@Test
	public void theTransportIsToldTheTempoAndPositionOfEachBlock() throws MidiPlayerException {
		sequence.addTempoInUSQ(0, 0, 1000000);
		transport.clear();
		transport.add(0, false, 120, 3, 4, 0, 0);
		playOnTheClockFrom(1920);
		blocks(1);
		MidiTransport.Span first = transport.get(0);
		String told = transport.count() + " " + first.isPlaying() + " " + first.getTempo() + " " + first.getQuarters() + " " + first.getSamples();
		blocks(1);

		assertEquals("1 true 60.0 1.0 44100", told);
		assertEquals(1 + 512 / 44100.0, transport.get(0).getQuarters(), 1e-9);
		assertEquals("@0 44612 3/4", "@" + transport.get(0).getFrame() + " " + transport.get(0).getSamples() + " " + transport.get(0).getNumerator() + "/" + transport.get(0).getDenominator());
	}

	static TGTimeSignature signature(int numerator, int denominator) {
		TGTimeSignature signature = new TGFactory().newTimeSignature();
		signature.setNumerator(numerator);
		signature.getDenominator().setValue(denominator);
		return signature;
	}

	// a time signature is no MIDI message: on the clock nothing is sent for it, and it does not make the sequence longer
	// (the sequencer stops after the first block, silencing again)
	@Test
	public void aTimeSignatureSendsNothingOnTheClock() throws MidiPlayerException {
		notes(0);
		sequence.addTimeSignature(10, 0, signature(3, 4));
		sequence.addTimeSignature(5000, 0, signature(6, 8));
		playToTheEndFrom(0);

		assertEquals("all notes off, pitch bend reset, @0 on 1 60 100 1 0, all notes off, pitch bend reset | false", blocks(1) + " | " + sequencer.isRunning());
	}

	// each span the transport was told for the last block: "@frame what q(thousandths of a quarter); "
	private String told(Function<MidiTransport.Span, String> what) {
		StringBuilder told = new StringBuilder();
		for (int i = 0; i < transport.count(); i++) {
			MidiTransport.Span span = transport.get(i);
			told.append("@" + span.getFrame() + " " + what.apply(span) + " q" + Math.round(span.getQuarters() * 1000) + "; ");
		}
		return told.toString();
	}

	private String signatures() {
		return told(span -> span.getNumerator() + "/" + span.getDenominator());
	}

	// the time signatures ("tick numerator/denominator") added, what the transport is told for the first block played
	// from the position
	private String signaturesToldPlayingFrom(long position, String... signatures) throws MidiPlayerException {
		for (String added : signatures) {
			String[] parts = added.split("[ /]");
			sequence.addTimeSignature(Long.parseLong(parts[0]), 0, signature(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
		}
		playOnTheClockFrom(position);
		blocks(1);
		return signatures();
	}

	// the transport is told a time signature from the frame its tick sounds on (tick 20: frame 459), in a span of its own
	// at the position reached there, and from the first frame of the blocks after
	@Test
	public void theTransportIsToldEachTimeSignatureFromItsFrame() throws MidiPlayerException {
		sequence.addTimeSignature(0, 0, signature(4, 4));
		sequence.addTimeSignature(980, 0, signature(3, 4));
		playOnTheClockFrom(960);
		blocks(1);
		String first = signatures();
		blocks(1);

		assertEquals("@0 4/4 q0; @459 3/4 q21; | @0 3/4 q23; ", first + "| " + signatures());
	}

	private String tempos() {
		return told(span -> span.getTempo() + " s" + span.getSamples());
	}

	// the transport is told a tempo from the frame its tick sounds on (tick 20 at 120 bpm: frame 459), in a span of its own
	// at its tick's position (0.0208 quarters: 918.75 samples at 60 bpm), and from the first frame of the blocks after
	@Test
	public void theTransportIsToldEachTempoFromItsFrame() throws MidiPlayerException {
		sequence.addTempoInUSQ(0, 0, 500000);
		sequence.addTempoInUSQ(980, 0, 1000000);
		playOnTheClockFrom(960);
		blocks(1);
		String first = tempos();
		blocks(1);

		assertEquals("@0 120.0 s0 q0; @459 60.0 s919 q21; | @0 60.0 s972 q22; ", first + "| " + tempos());
	}

	// playing from a position, the transport is told the time signature in force there from the first frame
	@Test
	public void playingFromAPositionIsToldTheTimeSignatureInForceThere() throws MidiPlayerException {
		assertEquals("@0 6/8 q1083; ", signaturesToldPlayingFrom(2000, "0 3/4", "1000 6/8", "9000 2/2"));
	}

	// playing from the tick of a time signature (where a measure starts), it holds from the first frame, in one span
	@Test
	public void playingFromTheTickOfATimeSignatureIsToldItInOneSpan() throws MidiPlayerException {
		assertEquals("@0 3/4 q4000; ", signaturesToldPlayingFrom(4800, "0 4/4", "4800 3/4"));
	}

	@Test
	public void playingFromAPositionSendsTheChannelSetupBeforeItOnTheFirstFrame() throws MidiPlayerException {
		sequence.addProgramChange(0, 0, 1, 25);
		sequence.addControlChange(0, 0, 1, 7, 90);
		notes(0, 970);
		playOnTheClockFrom(960);

		assertEquals("all notes off, pitch bend reset, @0 program 1 25, @0 cc 1 7 90, @230 on 1 61 100 1 0", blocks(1));
	}

	@Test
	public void movingWhilePlayingSilencesThenPlaysFromTheNewPosition() throws MidiPlayerException {
		sequence.addProgramChange(0, 0, 1, 25);
		notes(0, 2000, 2010);
		playOnTheClockFrom(0);
		blocks(1);
		sequencer.setTickPosition(2000);

		assertEquals("all notes off, pitch bend reset, @512 program 1 25, @512 on 1 61 100 1 0, @742 on 1 62 100 1 0", blocks(1));
	}

	@Test
	public void theEventsOfAMutedTrackAreNotSent() throws MidiPlayerException {
		note(10, 1, 61);
		sequencer.setMute(1, true);
		playOnTheClockFrom(0);
		blocks(1);

		assertEquals("[all notes off, pitch bend reset]", received.toString());
	}

	// a stop keeps the position reached (tick 44 after two blocks), where the player plays from again, and drops the
	// sequence (the player gives it a new one): started again without one, it silences and stops
	@Test
	public void stoppingKeepsThePositionReachedAndDropsTheSequence() throws MidiPlayerException {
		notes(50);
		playOnTheClockFrom(0);
		blocks(2);
		sequencer.stop();
		long stopped = sequencer.getTickPosition();
		sequencer.start();

		assertEquals("44 | all notes off, pitch bend reset, all notes off, pitch bend reset | false", stopped + " | " + blocks(1) + " | " + sequencer.isRunning());
	}

	@Test
	public void aMoveIsThePositionAtOnce() throws MidiPlayerException {
		playOnTheClockFrom(0);
		blocks(1);
		sequencer.setTickPosition(2000);

		assertEquals(2000, sequencer.getTickPosition());
	}

	// tick 10 sounds on frame 230; at 60 bpm the 282 frames left reach 6.1 ticks further
	@Test
	public void thePositionFollowsATempoChange() throws MidiPlayerException {
		sequence.addTempoInUSQ(10, 0, 1000000);
		playOnTheClockFrom(0);
		blocks(1);

		assertEquals(16, sequencer.getTickPosition());
	}

	// a block from tick 0 reaches tick 22 (512 / 22.96875 = 22.3)
	@Test
	public void thePositionIsTheTickReachedAtTheEndOfTheBlock() throws MidiPlayerException {
		playOnTheClockFrom(0);
		blocks(1);
		long first = sequencer.getTickPosition();
		blocks(1);

		assertEquals("22 44", first + " " + sequencer.getTickPosition());
	}

	// where the sequencer is after each of the blocks
	private String positions(int count) throws MidiPlayerException {
		StringBuilder positions = new StringBuilder();
		for (int b = 0; b < count; b++) {
			blocks(1);
			positions.append(positions.length() > 0 ? " " : "").append(sequencer.getTickPosition());
		}
		return positions.toString();
	}

	// the position is the tick the output sounds at the end of the block, 1024 frames behind: before the first two
	// blocks are heard it is before where playing started (512 frames before tick 0: tick -23)
	@Test
	public void thePositionIsTheTickTheOutputSoundsAtTheEndOfTheBlock() throws MidiPlayerException {
		delay = 1024;
		playOnTheClockFrom(0);

		assertEquals("-23 0 22 44", positions(4));
	}

	// a move is the position at once; the output sounds it 1024 frames after the next block starts
	@Test
	public void aMoveIsHeardTheDelayAfterTheNextBlockStarts() throws MidiPlayerException {
		delay = 1024;
		playOnTheClockFrom(0);
		blocks(3);
		sequencer.setTickPosition(2000);
		long moved = sequencer.getTickPosition();

		assertEquals("2000 | 1977 2000 2022", moved + " | " + positions(3));
	}

	// 60 bpm from the tick on, the output sounding the clock the delay late: the positions after the blocks played from
	// the position
	private String positionsAt60BpmFrom(long tick, int delay, long position, int blocks) throws MidiPlayerException {
		this.delay = delay;
		sequence.addTempoInUSQ(tick, 0, 1000000);
		playOnTheClockFrom(position);
		return positions(blocks);
	}

	// each on a sequencer of its own: 60 bpm from tick 10 (frame 230): frame 256, heard at the end of the first block, is
	// 0.57 of a 45.9 frame tick past it; the tempo in force where playing starts (60 bpm, set before tick 2000) is the one
	// the position runs at from there
	@Test
	public void thePositionRunsAtTheTempoTheOutputSounds() throws MidiPlayerException {
		String fromATempoChange = new TestMidiSequencerClock().positionsAt60BpmFrom(10, 256, 0, 1);
		String fromWherePlayingStarts = new TestMidiSequencerClock().positionsAt60BpmFrom(0, 512, 2000, 2);

		assertEquals("10 | 2000 2011", fromATempoChange + " | " + fromWherePlayingStarts);
	}

	// the last note is on tick 30: the sequencer stops once the output sounds past it, two blocks (1024 frames) later
	// than it would without the delay
	@Test
	public void theSequencerStopsOnceTheOutputSoundsPastTheLastEvent() throws MidiPlayerException {
		delay = 1024;
		notes(0, 30);
		playToTheEndFrom(0);
		int count = 0;
		while (listener != null && count < 10) {
			blocks(1);
			count++;
		}

		assertEquals("4 false", count + " " + sequencer.isRunning());
	}

	// the sequencer keeps the position on the audio thread, which must not allocate per block
	@Test
	public void keepingThePositionAllocatesNothingPerBlock() throws MidiPlayerException {
		com.sun.management.ThreadMXBean threads = (com.sun.management.ThreadMXBean) java.lang.management.ManagementFactory.getThreadMXBean();
		delay = 1024;
		sequence.addTempoInUSQ(0, 0, 500000);
		playOnTheClockFrom(0);
		blocks(1);
		long before = threads.getThreadAllocatedBytes(Thread.currentThread().getId());
		for (int b = 0; b < 1000; b++) {
			listener.beforeBlock(clock, BLOCK);
		}
		long allocated = threads.getThreadAllocatedBytes(Thread.currentThread().getId()) - before;

		assertEquals(true, allocated < 64 * 1024 && sequencer.getTickPosition() > 20000, allocated + " bytes allocated by 1000 blocks");
	}

	@Test
	public void onTheClockNoTimerRuns() throws MidiPlayerException {
		playOnTheClockFrom(0);

		assertEquals(0, timers.size());
		assertNotNull(this.listener);
	}

	@Test
	public void withoutAClockTheTimerRuns() throws MidiPlayerException {
		sequencer.setClock(clock);
		sequencer.setClock(null);
		sequencer.start();

		assertEquals(1, timers.size());
		assertNull(this.listener);
	}

	// past its last event the sequencer stops and leaves the clock
	@Test
	public void pastTheLastEventTheSequencerStopsAndLeavesTheClock() throws MidiPlayerException {
		note(0, 0, 60);
		note(30, 0, 61);
		playToTheEndFrom(0);
		blocks(1);
		String last = blocks(1);

		assertEquals("@689 on 1 61 100 1 0, all notes off, pitch bend reset", last);
		assertFalse(sequencer.isRunning());
		assertNull(this.listener);
	}

	@Test
	public void playingFromPastTheLastEventSendsTheChannelSetupThenStops() throws MidiPlayerException {
		sequence.addProgramChange(0, 0, 1, 25);
		playToTheEndFrom(960);

		assertEquals("all notes off, pitch bend reset, @0 program 1 25, all notes off, pitch bend reset", blocks(1));
		assertFalse(sequencer.isRunning());
	}

	// a note on tick 0 to the end of the sequence, tick 100 (frame 2297: the 250th of the fifth block), played from
	// tick 0 on the clock, looping from tick 0: whether the sequencer loops by itself
	private boolean loopingFromTheStart() throws MidiPlayerException {
		note(0, 0, 60);
		sequence.addNoteOff(100, 0, 1, 60, 0, 1, false);
		playToTheEndFrom(0);
		return sequencer.loop(0);
	}

	// the next pass starts on the frame the end sounds on, inside its block, after the end's own events, with nothing
	// silenced between the passes
	@Test
	public void aLoopPlaysItsStartAgainOnTheFrameItsEndSoundsOn() throws MidiPlayerException {
		boolean itself = loopingFromTheStart();
		blocks(4);

		assertEquals("true | @2297 off 1 60 0 1 0, @2297 on 1 60 100 1 0 | true 1", itself + " | " + blocks(1) + " | " + sequencer.isRunning() + " " + sequencer.getPasses());
	}

	// the block the next pass starts in is told two spans: this pass's end (from frame 2048, tick 89.2), the loop's
	// start (tick 0, quarter -1) from frame 249
	@Test
	public void theTransportIsToldTheLoopsStartFromTheFrameTheEndSoundsOn() throws MidiPlayerException {
		loopingFromTheStart();
		blocks(5);

		assertEquals("@0 true q-907; @249 true q-1000; ", told(span -> String.valueOf(span.isPlaying())));
	}

	// the next pass given (a Training Mode pass at 60 bpm) plays from the end on in place of this pass's events; the
	// pass after it plays it again: its 100 ticks last 4594 frames, to frame 6891 (block 14)
	@Test
	public void theNextPassGivenPlaysInPlaceOfThisPassFromWhereItStartsAgain() throws MidiPlayerException {
		loopingFromTheStart();
		MidiSequenceHandler next = sequencer.createNextPass(2);
		next.addTempoInUSQ(0, 0, 1000000);
		next.addNoteOn(0, 0, 1, 62, 100, 1, false);
		next.addNoteOff(100, 0, 1, 62, 0, 1, false);
		blocks(4);
		String second = blocks(1);
		blocks(8);

		assertEquals("@2297 off 1 60 0 1 0, @2297 on 1 62 100 1 0 | @6891 off 1 62 0 1 0, @6891 on 1 62 100 1 0", second + " | " + blocks(1));
	}

	// told the loop is over, or that it starts at the end (no pass to play again), the sequence ends at its end,
	// silencing, and the sequencer stops; each on a sequencer of its own
	@Test
	@org.junit.jupiter.api.Timeout(10)
	public void aLoopNoLongerAskedForOrWithoutAPassEndsAtTheEnd() throws MidiPlayerException {
		assertEquals("@2297 off 1 60 0 1 0, all notes off, pitch bend reset | false 0 / same", new TestMidiSequencerClock().endedLooping(-1) + " / "
			+ (new TestMidiSequencerClock().endedLooping(100).equals(new TestMidiSequencerClock().endedLooping(-1)) ? "same" : "not the same"));
	}

	// looping from the start, then told to loop from the tick: the fifth block | whether it runs, its passes
	private String endedLooping(long start) throws MidiPlayerException {
		loopingFromTheStart();
		sequencer.loop(start);
		blocks(4);
		return blocks(1) + " | " + sequencer.isRunning() + " " + sequencer.getPasses();
	}

	// on its own timer the sequencer does not loop: the player starts each pass
	@Test
	public void onTheTimerTheSequencerDoesNotLoopByItself() throws MidiPlayerException {
		sequencer.setClock(null);
		sequencer.start();

		assertFalse(sequencer.loop(0));
	}

	@Test
	public void stoppingLeavesTheClock() throws MidiPlayerException {
		playOnTheClockFrom(0);
		sequencer.stop();

		assertNull(this.listener);
	}
}
