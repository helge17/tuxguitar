package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.impl.sequencer.MidiSequencerImpl;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.thread.TGThreadManager;

/**
 * The player and TuxGuitar's sequencer on a synthesizer's sample clock, blocks of 512 frames rendered one at a time:
 * where the player is (the position the caret, the transport, Count In and the loops read) while it plays, after a
 * pause, and at the end of the song; where the notes start, and where a loop's next pass starts.
 *
 * The song has 4 measures of 4/4 at 120 bpm (a tick lasts 22.96875 frames at 44100 Hz) and a quarter note on the first
 * and on the last beat (frames 0 and 330750); its sequence ends at tick 16320 (frame 352800, in block 690). Its threads
 * run step by step (SteppedThreads).
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
	private float sampleRate = 44100;
	// what the clock's receivers were sent, each "@frame" counted from the first block; the first frame of the next block
	private final List<String> sent = new ArrayList<String>();
	private long blockStart;

	private final class ClockedSynthesizer implements MidiSynthesizer, MidiSampleClock {
		public MidiChannel openChannel(int channelId) { return null; }
		public void closeChannel(MidiChannel midiChannel) { }
		public boolean isChannelOpen(MidiChannel midiChannel) { return false; }
		public boolean isBusy() { return false; }
		public float getSampleRate() { return sampleRate; }
		public int getSoundingDelay() { return delay; }
		public void setListener(Listener listener) { TestMidiPlayerOnTheClock.this.listener = listener; }
		public MidiReceiver at(int frame) { return new MidiReceiverRecorder(sent, "@" + (blockStart + frame) + " "); }
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
			block();
			player.runPlayLoopProcess();
		}
		return player.getTickPosition();
	}

	private void block() throws MidiPlayerException {
		listener.beforeBlock(synthesizer, BLOCK);
		blockStart += BLOCK;
	}

	// the blocks the clock renders, the play loop taking a turn after each, until the play loop ends
	private int blocksToTheEnd() {
		return IntStream.rangeClosed(1, MOST_BLOCKS).filter(block -> this.lastTurn()).findFirst().orElse(MOST_BLOCKS);
	}

	// the clock renders a block, the play loop takes a turn: whether that ended the play loop
	private boolean lastTurn() {
		try {
			block();
			return !player.runPlayLoopProcess();
		} catch (MidiPlayerException e) {
			throw new IllegalStateException(e);
		}
	}

	// plays to the end: the blocks rendered | where the player is, whether it runs, what it notified meanwhile
	private String playedToTheEnd() throws MidiPlayerException {
		play();
		notified.clear();
		int blocks = blocksToTheEnd();
		return blocks + " | " + player.getTickPosition() + " " + player.isRunning() + " " + notified;
	}

	// plays to the end on a clock at the sample rate whose output sounds it the delay late
	private String playedToTheEnd(float rate, int late) throws MidiPlayerException {
		sampleRate = rate;
		delay = late;
		return playedToTheEnd();
	}

	// the frames the song's notes started on ("@frame"), in order
	private String notesStarted() {
		return sent.stream().filter(call -> call.contains(" on ")).map(call -> call.substring(0, call.indexOf(' '))).collect(Collectors.joining(" "));
	}

	// in loop mode, on a clock at the sample rate whose output sounds it the delay late, played for the blocks: where the
	// notes started | where the player is, whether it runs, what it notified meanwhile, the threads left to run
	private String looped(float rate, int late, int blocks) throws MidiPlayerException {
		sampleRate = rate;
		delay = late;
		player.getMode().setLoop(true);
		play();
		notified.clear();
		long position = blocks(blocks);
		return notesStarted() + " | " + position + " " + player.isRunning() + " " + notified + " " + threads.pending();
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

	// where playing ends, each on a player of its own. The song ends once the output sounds past the end of the
	// sequence: 1024 frames late, after block 692, its last notes heard; at 48000 Hz (the sequence's 15360 ticks last
	// 384000 frames, 750 blocks) and 512 frames late, the end of block 751 sounds exactly the end, which the song ends
	// past, after block 752
	@Test
	public void theSongEndsWhereItsEndIsHeard() throws MidiPlayerException {
		assertEquals("692 | 960 false [2] / 752 | 960 false [2]", fresh().playedToTheEnd(44100, 1024) + " / " + fresh().playedToTheEnd(48000, 512));
	}

	private static TestMidiPlayerOnTheClock fresh() {
		return new TestMidiPlayerOnTheClock();
	}

	// in loop mode the next pass starts on the very frame this one ends on (352800, inside block 690), the player going
	// on without a stop or a new start: after 700 blocks it is 5600 frames into the next pass (tick 1203), one loop
	// notified (5), no start left to run (the one play loop, which these turns stand for, still handed on)
	@Test
	public void inLoopModeTheNextPassStartsOnTheFrameThisOneEndsOn() throws MidiPlayerException {
		assertEquals("@0 @330750 @352800 | 1203 true [5] 0 started, 1 looped", looped(44100, 0, 700));
	}

	// at 48000 Hz the end falls on the first frame of block 750 (384000): the next pass starts there, whatever the delay
	@Test
	public void aPassEndingOnABlocksFirstFrameStartsTheNextThere() throws MidiPlayerException {
		assertEquals("@0 @360000 @384000", looped(48000, 1024, 760).split(" \\| ")[0]);
	}

	// the output 1024 frames late: after blocks 690 and 691 it still sounds this pass (frames 352256 and 352768: ticks
	// 16296 and 16318), after block 692 the next one, from its start on frame 352800 (frame 353280: tick 980)
	@Test
	public void inLoopModeThePlayerReportsThisPassUntilTheOutputSoundsTheNext() throws MidiPlayerException {
		looped(44100, 1024, 689);

		assertEquals("16296 16318 980", blocks(1) + " " + blocks(1) + " " + blocks(1));
	}

	// two passes started while the play loop took no turn (the second plays the next pass given once more) are two loops
	// at its next turn; the third pass's first note is on frame 705600
	@Test
	public void passesStartedBetweenTwoTurnsAreEachALoop() throws MidiPlayerException {
		looped(44100, 0, 1);
		for (int b = 0; b < 1400; b++) {
			block();
		}
		blocks(1);

		assertEquals("[5, 5] @0 @330750 @352800 @683550 @705600", notified + " " + notesStarted());
	}

	// Training Mode from 50% by 50: the first pass at 60 bpm ends on frame 705600, the next plays at 120 bpm from there,
	// its last beat on frame 1036350, and the 2030 blocks reach its tick 15491; the pass notified (5) as it starts
	@Test
	public void aTrainingModePassStartsAtItsOwnTempoOnTheFrameTheLastOneEnds() throws MidiPlayerException {
		player.getMode().setType(MidiPlayerMode.TYPE_CUSTOM);
		player.getMode().setCustomPercentFrom(50);
		player.getMode().setCustomPercentTo(100);
		player.getMode().setCustomPercentIncrement(50);
		player.getMode().reset();

		assertEquals("@0 @661500 @705600 @1036350 | 15491 true [5] 0 started, 1 looped", looped(44100, 0, 2030));
	}

	// Play looped turned off while playing: the pass under way plays to the end of the sequence, and the song ends there
	@Test
	public void aLoopTurnedOffWhilePlayingEndsWithItsPass() throws MidiPlayerException {
		looped(44100, 0, 100);
		player.getMode().setLoop(false);

		assertEquals("590 | 960 false [2]", blocksToTheEnd() + " | " + player.getTickPosition() + " " + player.isRunning() + " " + notified);
	}
}
