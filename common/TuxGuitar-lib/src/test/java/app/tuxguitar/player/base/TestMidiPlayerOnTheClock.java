package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.impl.sequencer.MidiSequencerImpl;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.thread.TGThreadManager;

/**
 * The player and TuxGuitar's sequencer on a synthesizer's sample clock, blocks of 512 frames rendered one at a time:
 * where the player is (the position the caret, the transport, Count In and the loops read) while it plays, after a
 * pause, and at the end of the song.
 *
 * The song has 4 measures of 4/4 at 120 bpm (a tick lasts 22.96875 frames at 44100 Hz) and a quarter note on the first
 * and on the last beat; its sequence ends at tick 16320. Its threads run step by step (SteppedThreads).
 */
public class TestMidiPlayerOnTheClock {

	private static final int BLOCK = 512;
	private static final int MOST_BLOCKS = 2000;

	private final PlayerSongFixture fixture = new PlayerSongFixture();
	private final MidiPlayer player = fixture.player;
	private final SteppedThreads threads = new SteppedThreads();
	private final List<String> notified = new ArrayList<String>();
	private MidiSampleClock.Listener listener;
	// how many frames after an event's frame the synthesizer's output sounds it
	private int delay;

	private final class ClockedSynthesizer implements MidiSynthesizer, MidiSampleClock {
		public MidiChannel openChannel(int channelId) { return null; }
		public void closeChannel(MidiChannel midiChannel) { }
		public boolean isChannelOpen(MidiChannel midiChannel) { return false; }
		public boolean isBusy() { return false; }
		public float getSampleRate() { return 44100; }
		public int getSoundingDelay() { return delay; }
		public void setListener(Listener listener) { TestMidiPlayerOnTheClock.this.listener = listener; }
		public MidiReceiver at(int frame) { return new MidiReceiverRecorder(new ArrayList<String>()); }
	}

	private final ClockedSynthesizer synthesizer = new ClockedSynthesizer();

	public TestMidiPlayerOnTheClock() {
		TGThreadManager.getInstance(fixture.context).setThreadHandler(threads);
		player.loadSequencer(new MidiSequencerImpl(fixture.context));
		player.getSynthesizerProxy().setMidiSynthesizer(synthesizer);
		player.addListener(event -> notified.add(String.valueOf((Integer) event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE))));
		quarter(1, 0);
		quarter(4, 3);
	}

	private void quarter(int measure, int beat) {
		TGVoice voice = fixture.beat(measure).getVoice(0);
		voice.getBeat().setStart(fixture.start(measure) + beat * TGDuration.QUARTER_TIME);
		voice.setEmpty(false);
		voice.getDuration().setValue(TGDuration.QUARTER);
		TGNote note = fixture.songManager.getFactory().newNote();
		note.setString(1);
		note.setValue(0);
		voice.addNote(note);
	}

	// plays, then runs the start: the sequencer starts on the clock
	private void play() throws MidiPlayerException {
		player.play();
		threads.runStarted();
		threads.runLoop();
	}

	// the clock renders the blocks, the play loop taking a turn after each: where the player is
	private long blocks(int count) throws MidiPlayerException {
		for (int b = 0; b < count; b++) {
			listener.beforeBlock(synthesizer, BLOCK);
			player.runPlayLoopProcess();
		}
		return player.getTickPosition();
	}

	// the blocks the clock renders, the play loop taking a turn after each, until the play loop ends
	private int blocksToTheEnd() throws MidiPlayerException {
		int count = 0;
		boolean playing = true;
		while (playing && count < MOST_BLOCKS) {
			listener.beforeBlock(synthesizer, BLOCK);
			playing = player.runPlayLoopProcess();
			count++;
		}
		return count;
	}

	// plays to the end: the blocks rendered | where the player is, whether it runs, what it notified meanwhile
	private String playedToTheEnd() throws MidiPlayerException {
		play();
		notified.clear();
		int blocks = blocksToTheEnd();
		return blocks + " | " + player.getTickPosition() + " " + player.isRunning() + " " + notified;
	}

	// 10 blocks from tick 960 reach 5120 frames: 222.9 ticks
	@Test
	public void whilePlayingThePlayerIsWhereTheSequencerIsAfterTheLastBlock() throws MidiPlayerException {
		play();

		assertEquals(1182, blocks(10));
	}

	// the output sounds the clock 1024 frames late: the player is where the output is, 4096 frames from tick 960
	@Test
	public void whilePlayingThePlayerIsWhereTheOutputIs() throws MidiPlayerException {
		delay = 1024;
		play();

		assertEquals(1138, blocks(10));
	}

	@Test
	public void afterAPausePlayingResumesWhereThePlayerWas() throws MidiPlayerException {
		play();
		blocks(10);
		player.pause();
		play();
		long resumed = player.getTickPosition();

		assertEquals("1182 1204", resumed + " " + blocks(1));
	}

	// resumed where the output was; until the output sounds the resumed play (1024 frames on) the player is before it
	@Test
	public void afterAPausePlayingResumesWhereTheOutputWas() throws MidiPlayerException {
		delay = 1024;
		play();
		blocks(10);
		player.pause();
		play();
		long resumed = player.getTickPosition();

		assertEquals("1138 1115 1138 1160", resumed + " " + blocks(1) + " " + blocks(1) + " " + blocks(1));
	}

	// the sequence ends at tick 16320, which the end of block 689 reaches (16319.9): the sequencer stops after block
	// 690, then the player returns to where it began and notifies the stop (2)
	@Test
	public void theSongEndsOnTheFirstBlockPastTheEndOfItsSequence() throws MidiPlayerException {
		assertEquals("690 | 960 false [2]", playedToTheEnd());
	}

	// the output sounds the end of the sequence two blocks (1024 frames) later: the song ends then, its last notes heard
	@Test
	public void theSongEndsOnceTheOutputSoundsPastTheEndOfItsSequence() throws MidiPlayerException {
		delay = 1024;

		assertEquals("692 | 960 false [2]", playedToTheEnd());
	}

	// the loop (5) plays again from the song's start: the sequencer is moved there, to start once the new start runs (the
	// first play loop, its turns taken here, is still handed on)
	@Test
	public void inLoopModeThePlayerPlaysTheSongAgainOnceItsEndIsReached() throws MidiPlayerException {
		player.getMode().setLoop(true);

		assertEquals("690 | 960 true [5] | 1 started, 1 looped", playedToTheEnd() + " | " + threads.pending());
	}

	@Test
	public void inLoopModeThePlayerPlaysTheSongAgainOnceTheOutputSoundsItsEnd() throws MidiPlayerException {
		delay = 1024;
		player.getMode().setLoop(true);

		assertEquals("692 | 960 true [5]", playedToTheEnd());
	}
}
