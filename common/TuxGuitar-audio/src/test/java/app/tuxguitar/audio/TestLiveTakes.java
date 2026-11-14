package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;

/**
 * Where an audio track's takes are time-stretched live: on the audio thread (the thread that renders the blocks, here
 * the test's own) or on the stretch-ahead worker (a thread named "worker", which catches up after each block, as it
 * does in the 11.6 ms a block lasts). The song has 4 measures of 4/4 at 120 bpm; the audio track has a take recorded at
 * 120 bpm over measure 1, so it is stretched whenever measure 1 plays at another Percentage of Tempo (at 80%, over
 * 110250 samples; at 50%, 176400; at 75%, 117600). The stretcher records the thread, the length of each stretch and the
 * samples it is given, and plays half the level of the first sample on each side.
 */
public class TestLiveTakes {

	private static final int M = 88200;
	private static final int BAR = 3840;
	private static final int FRAMES = 512;

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final MidiPlayerMode mode = MidiPlayer.getInstance(this.fixture.context).getMode();
	private final ExecutorService worker = Executors.newSingleThreadExecutor(task -> new Thread(task, "worker"));
	private final List<String> stretches = Collections.synchronizedList(new ArrayList<String>());
	// the samples each stretch was given, once each
	private final Set<float[][]> given = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<float[][], Boolean>()));
	private final TGLiveTakes takes = new TGLiveTakes(this.fixture.context, this.worker, rate -> this::stretch);
	private final MidiTransport transport = new MidiTransport();

	public TestLiveTakes() {
		this.fixture.measureCount(4);
		TGTrack track = this.fixture.songManager.addAudioTrack(this.fixture.song, TGAudioInput.mono(1));
		float[] samples = new float[2 * M];
		Arrays.fill(samples, 1f);
		track.getMeasure(0).addTakeClip(new TGTakeClip(new TGTake(1, samples, 44100), 0, BAR, 0, 120));
		this.takes.restoreParameters(Map.of(TGSynthChannel.TAKES_CHANNEL, Integer.toString(track.getChannelId())));
		this.takes.setSampleRate(44100);
		this.takes.setTransport(this.transport);
	}

	@AfterEach
	public void close() {
		this.takes.close();
	}

	private float[][] stretch(float[][] input, int from, int to, int length) {
		this.stretches.add(Thread.currentThread().getName() + " " + length);
		this.given.add(input);
		float[][] output = new float[input.length][length];
		for (int side = 0; side < input.length; side++) {
			Arrays.fill(output[side], input[side][from] / 2);
		}
		return output;
	}

	// Play looped over measure 1 at the Percentage of Tempo, Simple mode
	private void loopedAt(int percent) {
		this.mode.setType(MidiPlayerMode.TYPE_SIMPLE);
		this.mode.setSimplePercent(percent);
		this.mode.setLoop(true);
		this.mode.setLoopSHeader(1);
		this.mode.setLoopEHeader(1);
		this.mode.reset();
	}

	// plays from measure 1 for the samples, as the device tells the transport for each block what the mode plays there
	// (the jump back of each pass inside the block it falls in), the worker catching up after each block; then stops
	private void play(long samples) {
		long start = this.fixture.song.getMeasureHeader(0).getStart();
		TGTimeline device = new TGTimeline(this.fixture.song, TGSongTime.play(this.fixture.song, this.mode, start, Long.MAX_VALUE, 4), 44100);
		for (long from = 0; from < samples; from += FRAMES) {
			device.tell(this.transport, from, FRAMES);
			this.takes.process(new float[2][FRAMES]);
			this.catchUp();
		}
		this.transport.stop();
		this.takes.process(new float[2][FRAMES]);
	}

	private void catchUp() {
		try {
			this.worker.submit(() -> { }).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	private String audioThread() {
		return Thread.currentThread().getName();
	}

	// three passes at 80%: the one stretch is made when playing starts, by the block that first reaches it (the worker
	// has not got to it), and none after: the next passes' pieces, the same stretch, are not stretched again, at the
	// jumps back or anywhere
	@Test
	public void aLoopsPassesAtOneSpeedAreStretchedOnceAndNeverAtAJumpBack() {
		this.loopedAt(80);
		this.play(3 * 110250);

		assertEquals("[" + this.audioThread() + " 110250]", this.stretches.toString());
	}

	// Training Mode from 50% to 75% by 25: the second pass, at 75%, is stretched ahead by the worker while the first
	// plays (the timeline foresees it), and the jumps back find it made: the audio thread stretches nothing at them
	@Test
	public void theNextPassAtAnotherSpeedIsStretchedAheadAndFoundAtTheJumpBack() {
		this.loopedAt(50);
		this.mode.setType(MidiPlayerMode.TYPE_CUSTOM);
		this.mode.setCustomPercentFrom(50);
		this.mode.setCustomPercentTo(75);
		this.mode.setCustomPercentIncrement(25);
		this.mode.reset();
		this.play(176400 + 2 * 117600);

		assertEquals("[" + this.audioThread() + " 176400, worker 117600]", this.stretches.toString());
	}

	// played again from measure 1 at the same speed: what the last play stretched is not stretched again
	@Test
	public void playedAgainAtTheSameSpeedNothingIsStretchedAgain() {
		this.loopedAt(80);
		this.play(10 * FRAMES);
		this.play(10 * FRAMES);

		assertEquals("[" + this.audioThread() + " 110250]", this.stretches.toString());
	}

	// what the last play did not use is dropped (memory does not grow with each speed played): at 80%, at 50%, at 80%
	// again, the stretch at 80% is made again
	@Test
	public void aStretchTheLastPlayDidNotUseIsMadeAgain() {
		this.loopedAt(80);
		this.play(10 * FRAMES);
		this.loopedAt(50);
		this.play(10 * FRAMES);
		this.loopedAt(80);
		this.play(10 * FRAMES);

		assertEquals("[" + this.audioThread() + " 110250, " + this.audioThread() + " 176400, " + this.audioThread() + " 110250]", this.stretches.toString());
	}
}
