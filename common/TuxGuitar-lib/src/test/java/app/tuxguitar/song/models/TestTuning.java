package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Pins whether a range of notes fits a tuning (TGTuning.isWithinRange), and how tunings are ordered by priority
 * (TGTuning.compareTo): lower priority first, tunings without a priority last, and the comparison of two
 * prioritized tunings is the difference of their priorities.
 */
public class TestTuning {

	private static TGTuning tuning(String name, Integer priority) {
		TGTuning tuning = new TGTuning();
		tuning.setName(name);
		if (priority != null) {
			tuning.setPriority(priority);
		}
		return tuning;
	}

	// "priority vs otherPriority = result", null shown as none
	private static String compare(Integer priority, Integer otherPriority) {
		int result = tuning("a", priority).compareTo(tuning("b", otherPriority));
		return label(priority) + " vs " + label(otherPriority) + " = " + result;
	}

	private static String label(Integer priority) {
		return (priority == null ? "none" : priority.toString());
	}

	@Test
	public void comparingTwoTunings() {
		assertEquals(Arrays.asList(
				"none vs none = 0",
				"5 vs none = -1",
				"none vs 5 = 1",
				"2 vs 5 = -3",
				"5 vs 2 = 3",
				"4 vs 4 = 0"),
			Arrays.asList(
				compare(null, null),
				compare(5, null),
				compare(null, 5),
				compare(2, 5),
				compare(5, 2),
				compare(4, 4)));
	}

	private static boolean isWithinRange(int minVal, int maxVal, int... values) {
		TGTuning tuning = new TGTuning();
		tuning.setValues(values);
		return tuning.isWithinRange(minVal, maxVal);
	}

	@Test
	public void aRangeFitsWhenItLiesBetweenTheLowestAndHighestStrings() {
		// strings given high to low, as tunings are stored
		assertEquals(true, isWithinRange(40, 64, 64, 59, 55, 50, 45, 40));
		assertEquals(true, isWithinRange(45, 59, 64, 59, 55, 50, 45, 40));
		assertEquals(false, isWithinRange(39, 64, 64, 59, 55, 50, 45, 40));
		assertEquals(false, isWithinRange(40, 65, 64, 59, 55, 50, 45, 40));
		assertEquals(false, isWithinRange(0, 0));
	}

	@Test
	public void sortingPutsLowestPriorityFirstAndUnprioritizedLast() {
		List<TGTuning> tunings = new ArrayList<TGTuning>(Arrays.asList(
				tuning("none", null), tuning("ten", 10), tuning("one", 1), tuning("five", 5)));
		Collections.sort(tunings);

		List<String> names = new ArrayList<String>();
		for (TGTuning tuning : tunings) {
			names.add(tuning.getName());
		}
		assertEquals(Arrays.asList("one", "five", "ten", "none"), names);
	}
}
