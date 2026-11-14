package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.song.factory.TGFactory;

/**
 * Golden-master pins for TGDuration.splitPreciseDuration and splitPreciseDurationApproximately.
 * The expected digests were recorded from the original implementation (before its refactoring), and re-recorded
 * from upstream's implementation with its 9-tuplet fix (helge17/tuxguitar #1230) when that was merged in:
 * each digest covers every "input=output" line of one group, so any change in any split result fails the group.
 */
public class TestTGDurationSplitPins {

	private static final TGFactory FACTORY = new TGFactory();
	private static final long WHOLE = TGDuration.WHOLE_PRECISE_DURATION;
	private static final long[] MAX_TIMES = {WHOLE, WHOLE * 3 / 8, WHOLE / 8, WHOLE / 64, 0};
	private static final List<Integer> DURATION_LIMITS = Arrays.asList(null, 64, 16, 4);
	private static final List<Integer> DIVISION_LIMITS = Arrays.asList(null, 1, 3, 7, 9, 11, 13);
	private static final long[] SINGLE_TIMES = singleTimes();
	private static final long[] PAIR_TIMES = pairTimes();
	private static final long[] GRID_TIMES = gridTimes();

	private static final Map<String, String> EXPECTED = new HashMap<String, String>();
	static {
		EXPECTED.put("exact pairs", "276179af79d9a67053becc3b");
		EXPECTED.put("approximate pairs", "ce77605302eff5502b563dfe");
		EXPECTED.put("exact maxDivision=null", "268f24aa34b39a075c7b1d42");
		EXPECTED.put("exact maxDivision=1", "b0677c9f8be4f2f10469f9d2");
		EXPECTED.put("exact maxDivision=3", "0127a95d3030ebc0d0cfc5ef");
		EXPECTED.put("exact maxDivision=7", "c8f6e36a2c44d30f216e9d14");
		EXPECTED.put("exact maxDivision=9", "f226be1c2a63d9b37368d074");
		EXPECTED.put("exact maxDivision=11", "602b54863ecea8c7efe31444");
		EXPECTED.put("exact maxDivision=13", "af5178c2c1e0efbb263cad05");
		EXPECTED.put("approximate maxDivision=null", "23a8deecc49d62acb8cc71dd");
		EXPECTED.put("approximate maxDivision=1", "510f87bb7b2a3f5f238fcf84");
		EXPECTED.put("approximate maxDivision=3", "6e84dd9d57b270026c75d851");
		EXPECTED.put("approximate maxDivision=7", "7a20988b3cc6dc6b3bd62c89");
		EXPECTED.put("approximate maxDivision=9", "7f1063a66afca2701981b10c");
		EXPECTED.put("approximate maxDivision=11", "f58b196420b3f855cf1b696b");
		EXPECTED.put("approximate maxDivision=13", "9d49b888812d81f70c0df285");
	}

	private interface Splitter {
		List<TGDuration> split(long time, long max, Integer maxDurationValue, Integer maxDivision);
	}

	private static final Splitter EXACT = (time, max, value, division) ->
			TGDuration.splitPreciseDuration(time, max, FACTORY, value, division);
	private static final Splitter APPROXIMATE = (time, max, value, division) ->
			TGDuration.splitPreciseDurationApproximately(time, max, FACTORY, value, division);

	// zero needs no duration, even when the limits would allow none (negative max)
	@Test
	public void zeroIsSplitIntoNothingWhateverTheLimits() {
		assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			assertEquals(0, TGDuration.splitPreciseDuration(0, -1, FACTORY).size());
			assertEquals(0, TGDuration.splitPreciseDurationApproximately(0, -1, FACTORY, null, null).size());
		});
	}

	@Test
	public void aSingleTickCannotBeSplitExactly() {
		assertNull(TGDuration.splitPreciseDuration(1, WHOLE, FACTORY));
	}

	@TestFactory
	public Stream<DynamicTest> splitResultsMatchRecordedDigests() {
		Stream<DynamicTest> pairs = Stream.of(
				pin("exact pairs", () -> lines(PAIR_TIMES, EXACT, WHOLE, null, null)),
				pin("approximate pairs", () -> lines(PAIR_TIMES, APPROXIMATE, WHOLE, null, null)));
		Stream<DynamicTest> exactGrid = DIVISION_LIMITS.stream().map(division -> gridPin("exact", EXACT, division));
		Stream<DynamicTest> approximateGrid = DIVISION_LIMITS.stream().map(division -> gridPin("approximate", APPROXIMATE, division));
		return Stream.of(pairs, exactGrid, approximateGrid).flatMap(tests -> tests);
	}

	private static DynamicTest gridPin(String method, Splitter splitter, Integer division) {
		return pin(method + " maxDivision=" + division, () -> gridLines(splitter, division));
	}

	private static DynamicTest pin(String name, Supplier<Stream<String>> lines) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(EXPECTED.get(name), digest(lines.get()), name));
	}

	private static Stream<String> gridLines(Splitter splitter, Integer division) {
		return Arrays.stream(MAX_TIMES).boxed().flatMap(max -> DURATION_LIMITS.stream()
				.flatMap(value -> lines(GRID_TIMES, splitter, max, value, division)));
	}

	private static Stream<String> lines(long[] times, Splitter splitter, long max, Integer value, Integer division) {
		String parameters = " max=" + max + " value=" + value + " division=" + division + ": ";
		return Arrays.stream(times).mapToObj(time -> time + parameters + describe(splitter.split(time, max, value, division)));
	}

	private static String describe(List<TGDuration> durations) {
		return Optional.ofNullable(durations)
				.map(list -> list.stream().map(TestTGDurationSplitPins::describe).collect(Collectors.joining(",")))
				.orElse("null");
	}

	private static String describe(TGDuration duration) {
		return duration.getValue() + "|" + duration.isDotted() + "|" + duration.isDoubleDotted() + "|"
				+ duration.getDivision().getEnters() + ":" + duration.getDivision().getTimes();
	}

	private static String digest(Stream<String> lines) throws NoSuchAlgorithmException {
		byte[] text = lines.collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8);
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(text);
		return String.format("%064x", new BigInteger(1, hash)).substring(0, 24);
	}

	// precise time of every duration value, dotted state and time division
	private static long[] singleTimes() {
		return Arrays.stream(TGDivisionType.DIVISION_TYPES)
				.flatMap(division -> IntStream.rangeClosed(0, 6).boxed()
						.flatMap(power -> IntStream.rangeClosed(0, 2).mapToObj(dots -> newDuration(division, 1 << power, dots))))
				.mapToLong(TGDuration::getPreciseTime).distinct().sorted().toArray();
	}

	private static TGDuration newDuration(TGDivisionType division, int value, int dots) {
		TGDuration duration = FACTORY.newDuration();
		duration.setValue(value);
		duration.setDotted(dots == 1);
		duration.setDoubleDotted(dots == 2);
		duration.getDivision().copyFrom(division);
		return duration;
	}

	// every sum of two single durations
	private static long[] pairTimes() {
		return Arrays.stream(SINGLE_TIMES)
				.flatMap(first -> Arrays.stream(SINGLE_TIMES).map(second -> first + second))
				.distinct().sorted().toArray();
	}

	// single durations, their off-by-one neighbours, a sample of pairs and a few long times
	private static long[] gridTimes() {
		LongStream neighbours = Arrays.stream(SINGLE_TIMES).flatMap(time -> LongStream.of(time - 1, time, time + 1));
		LongStream sampledPairs = IntStream.range(0, PAIR_TIMES.length / 37).mapToLong(index -> PAIR_TIMES[index * 37]);
		LongStream longTimes = LongStream.of(2 * WHOLE, 3 * WHOLE, 2 * WHOLE + 1, WHOLE * 9 / 4, WHOLE * 16 / 9);
		return LongStream.concat(LongStream.concat(neighbours, sampledPairs), longTimes).distinct().toArray();
	}
}
