package app.tuxguitar.player.impl.sequencer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.player.base.MidiReceiverRecorder;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.player.base.MidiTransmitter;
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

	private final MidiSampleClock clock = new MidiSampleClock() {
		public float getSampleRate() {
			return 44100;
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
