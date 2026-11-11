package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;

/**
 * Pins TGDuration.fromTime(factory, time, minimum): the longest plain duration (whole, half, ... 64th) that fits
 * in a time given in ticks (QUARTER_TIME = 960 per quarter), or a copy of the minimum when even a 64th does not fit.
 *
 * A duration is written "value[.|..]/enters:times", e.g. "32./1:1" for a dotted 32nd.
 */
public class TestDurationFromTime {

	private final TGFactory factory = new TGFactory();

	private TGDuration dottedThirtySecond() {
		TGDuration duration = factory.newDuration();
		duration.setValue(TGDuration.THIRTY_SECOND);
		duration.setDotted(true);
		return duration;
	}

	private String fromTime(long time) {
		return describe(TGDuration.fromTime(factory, time, dottedThirtySecond()));
	}

	// the durations found for each of the times, space separated
	private String fromTimes(long... times) {
		StringBuilder found = new StringBuilder();
		for (long time : times) {
			found.append(found.length() == 0 ? "" : " ").append(fromTime(time));
		}
		return found.toString();
	}

	private static String describe(TGDuration duration) {
		TGDivisionType division = duration.getDivision();
		String dots = duration.isDotted() ? "." : (duration.isDoubleDotted() ? ".." : "");
		return duration.getValue() + dots + "/" + division.getEnters() + ":" + division.getTimes();
	}

	@Test
	public void givesTheLongestPlainDurationThatFits() {
		assertEquals("64/1:1 32/1:1 8/1:1 4/1:1 2/1:1 1/1:1", fromTimes(60, 120, 959, 960, 2000, 3840));
		// tuplet lengths, among them the ticks that once had their own lookup tolerance, round down too
		assertEquals("32./1:1 64/1:1 32/1:1 32/1:1 8/1:1 8/1:1", fromTimes(57, 95, 138, 177, 748, 854));
	}

	@Test
	public void givesTheMinimumWhenEvenASixtyFourthDoesNotFit() {
		TGDuration minimum = dottedThirtySecond();
		TGDuration found = TGDuration.fromTime(factory, 59, minimum);

		assertEquals("32./1:1", describe(found));
		assertNotSame(minimum, found);
		assertEquals("32./1:1", fromTime(0));
	}

	@Test
	public void givesAWholeForAnythingLongerThanAWhole() {
		assertEquals("1/1:1", fromTime(3841));
		// one tick past the precise length of an eighth: ticks are not precise times
		assertEquals("1/1:1", fromTime(TGDuration.WHOLE_PRECISE_DURATION / 8 + 1));
	}

	@Test
	public void theShortOverloadFallsBackToTheShortestTuplet() {
		assertEquals("64/7:4", describe(TGDuration.fromTime(factory, 59)));
		assertEquals("64/1:1", describe(TGDuration.fromTime(factory, 60)));
	}
}
