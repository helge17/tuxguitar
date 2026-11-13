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

	private final MidiTransport transport = new MidiTransport();
	private final MidiSampleClock clock = new MidiSampleClock() {
		public float getSampleRate() {
			return 44100;
		}

		public MidiTransport getTransport() {
			return transport;
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

	// a block from tick 0 reaches tick 22 (512 / 22.96875 = 22.3)
	@Test
	public void thePositionIsTheTickReachedAtTheEndOfTheBlock() throws MidiPlayerException {
		playOnTheClockFrom(0);
		blocks(1);
		long first = sequencer.getTickPosition();
		blocks(1);

		assertEquals("22 44", first + " " + sequencer.getTickPosition());
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

	@Test
	public void stoppingLeavesTheClock() throws MidiPlayerException {
		playOnTheClockFrom(0);
		sequencer.stop();

		assertNull(this.listener);
	}
}
