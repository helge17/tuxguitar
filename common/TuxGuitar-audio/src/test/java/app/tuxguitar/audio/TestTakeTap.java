package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;

/**
 * What an audio track's tap holds, sample for sample, at the crossfade rules' bounds. The takes hold constant levels
 * (one per measure of the take), so the tap shows each take's gain exactly: a take of zeros still plays, silently, so
 * rendering once with one take at a level and the other silent shows that one's fade alone. The fade is 256 samples
 * long: going to a newer take it fades in from 1/256 at its first sample to 1 at its 256th, while the older fades out
 * from 255/256 to 0 over the same samples, reading on past where it stopped.
 *
 * The song has 4 measures of 4/4 at 120 bpm: 88200 samples each at 44100 Hz. Clips are made by hand, measure by
 * measure, as recording and editing make them. The stretcher used when the tempo differs plays, on each side, half the
 * level of the first sample it is given, and keeps what it was asked for, so a stretched stretch tells itself from the
 * take's own samples. The tap has two sides; a mono take sounds on both, a stereo one on its own.
 */
public class TestTakeTap {

	private static final int M = 88200;
	private static final int BAR = 3840;
	private static final double F = 256;

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack track;
	private final List<String> stretches = new ArrayList<String>();

	private final TGTimeStretcher halfOfTheFirstSample = (input, from, to, length) -> {
		stretches.add(from + "-" + to + ":" + length);
		float[][] output = new float[input.length][length];
		for (int side = 0; side < input.length; side++) {
			Arrays.fill(output[side], input[side][from] / 2);
		}
		return output;
	};

	public TestTakeTap() {
		for (int m = 1; m < 4; m++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		track = songManager.addAudioTrack(song, TGAudioInput.mono(1));
	}

	// a take of the given length holding one level per 88200 samples, the last one to its end
	private static TGTake take(int number, int length, float... levels) {
		float[] samples = new float[length];
		for (int i = 0; i < length; i++) {
			samples[i] = levels[Math.min(i / M, levels.length - 1)];
		}
		return new TGTake(number, samples, 44100);
	}

	// a clip of the take in the measure (from 1), from tick to tick of it, recorded at 120 bpm
	private void clip(TGTake take, int measure, long start, long end, int firstSample) {
		clip(take, measure, start, end, firstSample, 120);
	}

	private void clip(TGTake take, int measure, long start, long end, int firstSample, double tempo) {
		track.getMeasure(measure - 1).addTakeClip(new TGTakeClip(take, start, end, firstSample, tempo));
	}

	// a clip in each measure from the first to the last, the take's samples following on
	private void clips(TGTake take, int first, int last) {
		for (int m = first; m <= last; m++) {
			clip(take, m, 0, BAR, (m - first) * M);
		}
	}

	// the song played from a tick to another (from the start of measure 1), at a Percentage of Tempo
	private TGTimeline played(MidiPlayerMode mode, long start, long stop, int passes) {
		long origin = song.getMeasureHeader(0).getStart();
		return new TGTimeline(song, TGSongTime.play(song, mode, origin + start, origin + stop, passes), 44100);
	}

	private TGTimeline wholeSong(int percent) {
		MidiPlayerMode mode = new MidiPlayerMode();
		mode.setCurrentPercent(percent);
		return played(mode, 0, 4 * BAR, 1);
	}

	// the left side of the tap
	private float[] tap(TGTimeline timeline, TGTrackSource[] sources, float[][] input) {
		return TGTakeTap.render(track, timeline, halfOfTheFirstSample, sources, input)[0];
	}

	private float[] tap(TGTimeline timeline) {
		return sides(timeline)[0];
	}

	private float[][] sides(TGTimeline timeline) {
		TGTrackSource[] sources = new TGTrackSource[timeline.count()];
		Arrays.fill(sources, TGTrackSource.TAKES);
		return TGTakeTap.render(track, timeline, halfOfTheFirstSample, sources, null);
	}

	// a take recorded at 44100 Hz on a timeline at 48000 Hz is resampled to it: each of its samples sounds at the same
	// time (its sample 110250, beat 2 of measure 2, at 120000), at its level; at another tempo the stretcher is given
	// the take's samples at 48000 Hz (measure 3 recorded at 240 bpm: its samples 192000 to 240000)
	@Test
	public void aTakeRecordedAtAnotherSampleRatePlaysAtTheSameTimes() {
		float[] samples = new float[4 * M];
		samples[M + 22050] = 1f;
		clips(new TGTake(1, samples, 44100), 1, 2);
		clip(new TGTake(2, samples, 44100), 3, 0, BAR, 2 * M, 240);
		long origin = song.getMeasureHeader(0).getStart();
		float[] tap = sides(new TGTimeline(song, TGSongTime.play(song, new MidiPlayerMode(), origin, origin + 3 * BAR, 1), 48000))[0];

		assertEquals("288000 1.0", tap.length + " " + tap[120000]);
		assertEquals("[192000-240000:96000]", stretches.toString());
	}

	// a part of a block holds what the tap sounds over its frames alone, from the sample the part starts on: the take's
	// sample M + 100 lands 50 frames into the part, its sample M + 300 is past the part's 100 frames
	@Test
	public void aPartOfABlockHoldsWhatTheTapSoundsOverItsFrames() {
		float[] samples = new float[4 * M];
		samples[M + 100] = 1f;
		samples[M + 300] = 0.5f;
		clips(new TGTake(1, samples, 44100), 1, 2);
		TGTimeline timeline = wholeSong(100);
		TGTrackSource[] sources = new TGTrackSource[timeline.count()];
		Arrays.fill(sources, TGTrackSource.TAKES);
		TGTakeTap tap = TGTakeTap.of(track, timeline, halfOfTheFirstSample, sources, new TGTapInput(0, null));
		float[][] whole = new float[2][512];
		tap.render(whole, M + 50);
		float[][] part = new float[2][512];
		tap.render(part, 200, 100, M + 50);

		assertEquals("1.0 0.5 | 0.0 1.0 0.0", whole[0][50] + " " + whole[0][250] + " | " + part[0][50] + " " + part[0][250] + " " + part[0][450]);
	}

	private static void assertSamples(double[] expected, float[] tap, int... at) {
		double[] actual = new double[at.length];
		for (int i = 0; i < at.length; i++) {
			actual[i] = tap[at[i]];
		}
		assertArrayEquals(expected, actual, 1e-6, "samples " + Arrays.toString(at));
	}

	// take 1 over the song (level 1 in measure 1, 0.5 after), take 2 over measure 2 at the level given
	private float[] olderUnderNewer(float older, float newer) {
		TGTake take1 = take(1, 4 * M, older, older / 2);
		clips(take1, 1, 4);
		clips(take(2, M, newer), 2, 2);
		return tap(wholeSong(100));
	}

	@Test
	public void theOlderTakeFadesOutOverTheNewerOnesFirstSamplesAndBackInOverItsLast() {
		float[] tap = olderUnderNewer(1, 0);

		// reading on from where it stopped (its level 0.5), then before where it plays again
		assertSamples(new double[] {1, 0.5 * 255 / F, 0.5 * 254 / F, 0, 0}, tap, M - 1, M, M + 1, M + 255, M + 256);
		assertSamples(new double[] {0, 0.5 / F, 0.5, 0.5}, tap, 2 * M - 257, 2 * M - 256, 2 * M - 1, 2 * M);
	}

	@Test
	public void theNewerTakeFadesInOverItsFirstSamplesAndOutOverItsLast() {
		float[] tap = olderUnderNewer(0, 1);

		assertSamples(new double[] {0, 1 / F, 1, 1}, tap, M - 1, M, M + 255, M + 256);
		assertSamples(new double[] {1, 255 / F, 0, 0}, tap, 2 * M - 257, 2 * M - 256, 2 * M - 1, 2 * M);
	}

	// at 50% a measure lasts 176400 samples: inside its stretch the take plays what the stretcher made of it, around
	// it (where it fades on past its stretch) its own samples
	@Test
	public void aStretchedTakeFadesOnItsOwnSamplesAroundItsStretch() {
		TGTake take1 = take(1, 4 * M, 1, 0.5f, 0.25f, 0.125f);
		clips(take1, 1, 4);
		clips(take(2, M, 0), 2, 2);
		int measure = 2 * M;
		float[] tap = tap(wholeSong(50));

		assertSamples(new double[] {0.5, 0.5, 0.5 * 255 / F, 0}, tap, 0, measure - 1, measure, measure + 256);
		assertSamples(new double[] {0.5 / F, 0.5, 0.125}, tap, 2 * measure - 256, 2 * measure - 1, 2 * measure);
		assertEquals(List.of("0-88200:176400", "0-88200:176400", "176400-264600:176400", "264600-352800:176400"), stretches);
	}

	// a stretch of no samples of the take (its clip starts past them) is silence, not a stretch
	@Test
	public void aClipPastTheEndOfItsTakeIsSilent() {
		clip(take(1, 10, 1), 1, 0, BAR, 1000, 60);
		float[] tap = tap(wholeSong(100));

		assertEquals(0, Meters.peak(tap, 0, tap.length));
		assertEquals(List.of(), stretches);
	}

	// as when a measure inside the take was removed: the take jumps to other samples, so it is cut
	@Test
	public void aTakeCutUnderANewerOneFadesOutBeforeIt() {
		TGTake take1 = take(1, 4 * M, 1);
		clip(take1, 1, 0, BAR, 0);
		clip(take1, 2, 0, BAR, 2 * M);
		clips(take(2, M, 0), 2, 2);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {1, 255 / F, 0, 0}, tap, M - 257, M - 256, M - 1, M);
	}

	@Test
	public void aNewerTakeOverACutTakeStartsAtFullLevel() {
		TGTake take1 = take(1, 4 * M, 0);
		clip(take1, 1, 0, BAR, 0);
		clip(take1, 2, 0, BAR, 2 * M);
		clips(take(2, M, 1), 2, 2);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {0, 1, 1}, tap, M - 1, M, 2 * M - 1);
	}

	// rounding can put a measure's first sample of the take one off from where the measure before stopped
	@Test
	public void aTakeGoingOnWithinASampleIsNotFaded() {
		// its level changes where it goes on (1, then 0.5), so a crossfade with itself would show
		TGTake take1 = take(1, 4 * M, 1, 0.5f);
		clip(take1, 1, 0, BAR, 0);
		clip(take1, 2, 0, BAR, M + 1);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {1, 1, 0.5}, tap, M - 256, M - 1, M);
	}

	@Test
	public void aTakeGoingOnWithinASampleUnderANewerOneFadesOutAfterItsStart() {
		TGTake take1 = take(1, 4 * M, 1);
		clip(take1, 1, 0, BAR, 0);
		clip(take1, 2, 0, BAR, M + 1);
		clips(take(2, M, 0), 2, 2);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {1, 255 / F}, tap, M - 1, M);
	}

	@Test
	public void aTakeEndingWithinASampleOfWhereItStopsIsNotFaded() {
		clip(take(1, M + 1, 1), 1, 0, BAR, 0);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {1, 0}, tap, M - 1, M);
	}

	// reading on under a newer take past the last sample of the older one reads silence
	@Test
	public void anOlderTakeShorterThanItsClipsFadesOutIntoSilence() {
		TGTake take1 = take(1, M + 100, 1);
		clip(take1, 1, 0, BAR, 0);
		clip(take1, 2, 0, BAR, M);
		clips(take(2, M, 0), 2, 2);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {1 - 100 / F, 0}, tap, M + 99, M + 100);
	}

	// the punch range is measure 2: the monitored input counts as the newer take there
	private float[] punchedIn(float take, float input, TGTrackSource source) {
		clips(take(1, 4 * M, take), 1, 4);
		float[] monitored = new float[4 * M];
		Arrays.fill(monitored, input);
		TGTrackSource[] sources = {TGTrackSource.TAKES, source, TGTrackSource.TAKES, TGTrackSource.TAKES};
		return tap(wholeSong(100), sources, new float[][] {monitored});
	}

	@Test
	public void theTakeFadesOutUnderTheMonitoredInputAndBackInBeforeIt() {
		float[] tap = punchedIn(1, 0, TGTrackSource.INPUT);

		assertSamples(new double[] {255 / F, 0}, tap, M, M + 256);
		assertSamples(new double[] {1 / F, 1, 1}, tap, 2 * M - 256, 2 * M - 1, 2 * M);
	}

	@Test
	public void theMonitoredInputFadesInAndOutAsANewerTake() {
		float[] tap = punchedIn(0, 1, TGTrackSource.INPUT);

		assertSamples(new double[] {0, 1 / F, 1}, tap, M - 1, M, M + 255);
		assertSamples(new double[] {255 / F, 0}, tap, 2 * M - 256, 2 * M - 1);
	}

	// with monitoring Off nothing is heard where the track records: the take is cut there
	@Test
	public void theTakeFadesOutBeforeWhereTheTrackRecordsUnheardAndComesBackAtFullLevel() {
		float[] tap = punchedIn(1, 1, TGTrackSource.SILENCE);

		assertSamples(new double[] {255 / F, 0, 0, 0, 1}, tap, M - 256, M - 1, M, 2 * M - 1, 2 * M);
	}

	// Play looped over measures 2 to 3, the take covering exactly them: it has no more audio at the jump back, and
	// still fades out before it
	@Test
	public void atTheJumpBackOfALoopTheTakeFadesOutEvenAtItsEnd() {
		clips(take(1, 2 * M, 1), 2, 3);
		MidiPlayerMode mode = new MidiPlayerMode();
		mode.setLoop(true);
		mode.setLoopSHeader(2);
		mode.setLoopEHeader(3);
		float[] tap = tap(played(mode, BAR, Long.MAX_VALUE / 2, 2));

		assertSamples(new double[] {255 / F, 0, 1}, tap, 2 * M - 256, 2 * M - 1, 2 * M);
	}

	// the first take chosen for measure 2, the second for measure 3; between two chosen takes the one recorded later
	// (the higher number) is the newer
	private float[] chosen(TGTake inMeasure2, TGTake inMeasure3) {
		track.getMeasure(1).setChosenTake(inMeasure2);
		track.getMeasure(2).setChosenTake(inMeasure3);
		return tap(wholeSong(100));
	}

	// takes 1 and 2 over the song at their levels, take 2 chosen for measure 2 and take 1 for measure 3
	private float[] newerChosenFirst(float older, float newer) {
		TGTake take1 = take(1, 4 * M, older);
		TGTake take2 = take(2, 4 * M, newer);
		clips(take1, 1, 4);
		clips(take2, 1, 4);
		return chosen(take2, take1);
	}

	@Test
	public void betweenTwoChosenTakesTheNewerFadesOutOverItsLastSamplesWhileTheOlderFadesIn() {
		int[] at = {2 * M - 257, 2 * M - 256, 2 * M - 1, 2 * M};

		assertSamples(new double[] {0, 1 / F, 1, 1}, new TestTakeTap().newerChosenFirst(1, 0), at);
		assertSamples(new double[] {1, 255 / F, 0, 0}, new TestTakeTap().newerChosenFirst(0, 1), at);
	}

	@Test
	public void betweenTwoChosenTakesTheNewerFadesInOverItsFirstSamplesWhileTheOlderFadesOut() {
		TGTake take1 = take(1, 4 * M, 1);
		TGTake take2 = take(2, 4 * M, 0.5f);
		clips(take1, 1, 4);
		clips(take2, 1, 4);
		float[] tap = chosen(take1, take2);

		assertSamples(new double[] {1, 255 / F + 0.5 / F, 0.5, 0.5}, tap, 2 * M - 1, 2 * M, 2 * M + 255, 2 * M + 256);
	}

	// take 1 over measures 1 and 2 only, so it has no audio past measure 2; take 2 over the song
	@Test
	public void betweenTwoChosenTakesTheNewerFadesInWhereTheOlderHasNoAudio() {
		TGTake take1 = take(1, 2 * M, 0);
		TGTake take2 = take(2, 4 * M, 1);
		clips(take1, 1, 2);
		clips(take2, 1, 4);
		float[] tap = chosen(take1, take2);

		assertSamples(new double[] {0, 1 / F, 1}, tap, 2 * M - 1, 2 * M, 2 * M + 255);
	}

	// take 2 over measures 1 and 2 only, take 1 over measures 3 and 4 only: the older has no audio before measure 3
	@Test
	public void betweenTwoChosenTakesTheNewerFadesOutWhereTheOlderHasNoAudioAndTheOlderStartsAtFullLevel() {
		TGTake take1 = take(1, 2 * M, 0.5f);
		TGTake take2 = take(2, 2 * M, 1);
		clips(take2, 1, 2);
		clips(take1, 3, 4);
		float[] tap = chosen(take2, take1);

		assertSamples(new double[] {255 / F, 0, 0.5}, tap, 2 * M - 256, 2 * M - 1, 2 * M);
	}

	// take 2 over measures 1 and 2 only (not chosen), take 1 over measures 3 and 4 only, chosen for measure 3: a chosen
	// take after one that is not is newer, but starts against silence, so at full level
	@Test
	public void aChosenTakeStartingWhereATakeNotChosenEndsStartsAtFullLevel() {
		TGTake take1 = take(1, 2 * M, 1);
		clips(take(2, 2 * M, 0), 1, 2);
		clips(take1, 3, 4);
		track.getMeasure(2).setChosenTake(take1);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {0, 1, 1}, tap, 2 * M - 1, 2 * M, 2 * M + 1);
	}

	// take 1 chosen for measures 2 and 3, its audio of a removed measure cut out between them: the audio before the cut
	// fades out, the audio after starts at full level
	@Test
	public void whereAChosenTakeIsCutTheAudioAfterTheCutStartsAtFullLevel() {
		TGTake take1 = take(1, 4 * M, 1, 1, 0.5f);
		clip(take1, 2, 0, BAR, 0);
		clip(take1, 3, 0, BAR, 2 * M);
		track.getMeasure(1).setChosenTake(take1);
		track.getMeasure(2).setChosenTake(take1);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {255 / F, 0, 0.5, 0.5}, tap, 2 * M - 256, 2 * M - 1, 2 * M, 2 * M + 1);
	}

	// take 1 recorded to measure 2 beat 3, take 2 from there: one ends as the other starts, nothing fades
	private float[] endToStart(float older, float newer) {
		TGTake take1 = take(1, M + M / 2, older);
		clip(take1, 1, 0, BAR, 0);
		clip(take1, 2, 0, BAR / 2, M);
		clip(take(2, M / 2, newer), 2, BAR / 2, BAR, 0);
		return tap(wholeSong(100));
	}

	// the older is not faded, the newer starts at full level
	@Test
	public void whereATakeEndsAsANewerStartsNothingFades() {
		int[] at = {M + M / 2 - 1, M + M / 2};

		assertSamples(new double[] {1, 0}, new TestTakeTap().endToStart(1, 0), at);
		assertSamples(new double[] {0, 1}, new TestTakeTap().endToStart(0, 1), at);
	}

	// take 2 over measure 2 only, take 1 over measures 3 and 4 only
	@Test
	public void aNewerTakeEndingWhereAnOlderStartsIsNotFaded() {
		clips(take(2, M, 1), 2, 2);
		clips(take(1, 2 * M, 0), 3, 4);
		float[] tap = tap(wholeSong(100));

		assertSamples(new double[] {1, 0}, tap, 2 * M - 1, 2 * M);
	}

	// the song's length of a level on the left and another on the right
	private static float[][] stereo(float left, float right) {
		float[][] sides = new float[2][4 * M];
		Arrays.fill(sides[0], left);
		Arrays.fill(sides[1], right);
		return sides;
	}

	// a stereo take at 1 on the left and 0.5 on the right, take 2 (mono, silent) over measure 2: each side fades as
	// the take does
	@Test
	public void aStereoTakeSoundsEachSideOnItsOwnSideOfTheTap() {
		clips(new TGTake(1, stereo(1, 0.5f), 44100), 1, 4);
		clips(take(2, M, 0), 2, 2);
		float[][] tap = sides(wholeSong(100));

		assertSamples(new double[] {1, 255 / F}, tap[0], M - 1, M);
		assertSamples(new double[] {0.5, 0.5 * 255 / F}, tap[1], M - 1, M);
	}

	@Test
	public void aMonoTakeSoundsOnBothSides() {
		clips(take(1, 4 * M, 1, 0.5f), 1, 4);
		float[][] tap = sides(wholeSong(100));

		assertArrayEquals(tap[0], tap[1]);
		assertSamples(new double[] {1, 0.5}, tap[1], M - 1, M);
	}

	// at 50% each measure is stretched once, both sides together
	@Test
	public void aStretchedStereoTakeIsStretchedOnBothSidesAtOnce() {
		clips(new TGTake(1, stereo(1, 0.5f), 44100), 1, 4);
		float[][] tap = sides(wholeSong(50));

		assertSamples(new double[] {0.5}, tap[0], 0);
		assertSamples(new double[] {0.25}, tap[1], 0);
		assertEquals(4, stretches.size());
	}

	@Test
	public void aMonitoredPairOfInputChannelsSoundsOnItsSides() {
		float[][] input = stereo(1, 0.5f);
		TGTrackSource[] sources = new TGTrackSource[4];
		Arrays.fill(sources, TGTrackSource.INPUT);
		float[][] tap = TGTakeTap.render(track, wholeSong(100), halfOfTheFirstSample, sources, input);

		assertSamples(new double[] {1, 1}, tap[0], 0, 4 * M - 1);
		assertSamples(new double[] {0.5, 0.5}, tap[1], 0, 4 * M - 1);
	}

	// live, the input is only the block's: read from the sample of the tap the block starts on
	@Test
	public void liveTheMonitoredInputIsTheBlocksFromWhereItStarts() {
		TGTrackSource[] sources = new TGTrackSource[4];
		Arrays.fill(sources, TGTrackSource.INPUT);
		TGTapInput input = new TGTapInput(0, null);
		TGTakeTap tap = TGTakeTap.of(track, wholeSong(100), halfOfTheFirstSample, sources, input);
		float[][] block = new float[2][2];
		input.set(M + 1000, new float[][] {{0.25f, 0.75f}});
		tap.render(block, M + 1000);

		assertArrayEquals(new float[] {0.25f, 0.75f}, block[0]);
		assertArrayEquals(new float[] {0.25f, 0.75f}, block[1]);
	}

	private TGTakeTap takesOf(TGTimeline timeline) {
		TGTrackSource[] sources = new TGTrackSource[timeline.count()];
		Arrays.fill(sources, TGTrackSource.TAKES);
		return TGTakeTap.of(track, timeline, halfOfTheFirstSample, sources, null);
	}

	// live, the tap is rendered a block at a time, from the sample of the timeline a block starts on (silence before
	// the timeline): the same samples as rendered whole, crossfades included
	@Test
	public void renderedABlockAtATimeTheTapIsTheSameAsRenderedWhole() {
		clips(take(1, 4 * M, 1, 0.5f), 1, 4);
		clip(take(2, M, 0.25f), 2, 0, BAR / 2, 0);
		TGTimeline timeline = wholeSong(100);
		float[][] whole = sides(timeline);
		TGTakeTap tap = takesOf(timeline);
		float[][] blocks = new float[2][whole[0].length];
		float[][] first = new float[2][TGBlocks.FRAMES];
		tap.render(first, -300);
		for (long from = -300; from < whole[0].length; from += TGBlocks.FRAMES) {
			float[][] block = new float[2][TGBlocks.FRAMES];
			tap.render(block, from);
			TGBlocks.place(block, blocks, from);
		}
		assertArrayEquals(new float[300], Arrays.copyOf(first[0], 300));
		assertEquals(1f, first[0][300], 0f);
		assertArrayEquals(whole[0], blocks[0]);
		assertArrayEquals(whole[1], blocks[1]);
	}

	// a stretch is made when a block first reaches it, not before
	@Test
	public void aStretchIsMadeWhenABlockFirstReachesIt() {
		clips(take(1, 4 * M, 1), 1, 4);
		TGTakeTap tap = takesOf(wholeSong(50));
		String before = stretches.toString();
		tap.render(new float[2][TGBlocks.FRAMES], 0);
		tap.render(new float[2][TGBlocks.FRAMES], TGBlocks.FRAMES);
		assertEquals("[] [0-88200:176400]", before + " " + stretches);
	}

	// playing stops at measure 1 beat 3, where take 2 ends over take 1: the tap ends at full level
	@Test
	public void whereThePlayingStopsNothingFades() {
		clips(take(1, M, 0), 1, 1);
		clip(take(2, M / 2, 1), 1, 0, BAR / 2, 0);
		float[] tap = tap(played(new MidiPlayerMode(), 0, BAR / 2, 1));

		assertEquals(M / 2, tap.length);
		assertSamples(new double[] {1}, tap, M / 2 - 1);
	}
}
