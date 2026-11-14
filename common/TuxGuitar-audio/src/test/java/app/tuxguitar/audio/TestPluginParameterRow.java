package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * A row of TuxGuitar's parameter editor (a plugin without an editor of its own): a slider over the parameter's range, in
 * its steps or, continuous, in 1000 positions, and the plain value typed, taken only inside the range.
 */
public class TestPluginParameterRow {

	// Test Gain's Gain: 0 to 16, continuous, at 2
	private static final TGPluginParameterRow GAIN = new TGPluginParameterRow(1, "Gain", 2.0, 0.0, 16.0, 0);
	// a list of 3 entries
	private static final TGPluginParameterRow MODE = new TGPluginParameterRow(2, "Mode", 1.0, 0.0, 2.0, 2);
	// a range that falls (its normalized 0 is its largest plain value)
	private static final TGPluginParameterRow FALLING = new TGPluginParameterRow(3, "Damping", 10.0, 10.0, 0.0, 0);

	private static String positions(TGPluginParameterRow row, double... plains) {
		return Arrays.stream(plains).mapToObj(plain -> String.valueOf(row.position(plain))).collect(Collectors.joining(" "));
	}

	private static String plains(TGPluginParameterRow row, int... positions) {
		return Arrays.stream(positions).mapToObj(position -> String.valueOf(row.plain(position))).collect(Collectors.joining(" "));
	}

	// an on/off switch is one step
	@Test
	public void aContinuousParameterSlidesIn1000PositionsASteppedOneInItsSteps() {
		TGPluginParameterRow bypass = new TGPluginParameterRow(5, "Bypass", 0.0, 0.0, 1.0, 1);
		assertEquals("1000 2 1", GAIN.positions() + " " + MODE.positions() + " " + bypass.positions());
	}

	// the slider's position of a plain value, kept inside the slider
	@Test
	public void aPlainValueIsTheSlidersPositionAlongTheRange() {
		assertEquals("125 0 1000 0 1000 | 1 2 | 750 0 1000", positions(GAIN, 2.0, 0.0, 16.0, -1.0, 20.0) + " | " + positions(MODE, 1.0, 2.0) + " | " + positions(FALLING, 2.5, 10.0, 0.0));
	}

	@Test
	public void aSlidersPositionIsAPlainValueAlongTheRange() {
		assertEquals("8.0 0.0 16.0 | 0.0 1.0 2.0 | 2.5", plains(GAIN, 500, 0, 1000) + " | " + plains(MODE, 0, 1, 2) + " | " + plains(FALLING, 750));
	}

	// a range of one value has its one position
	@Test
	public void aParameterOfOneValueHasOnePosition() {
		TGPluginParameterRow fixed = new TGPluginParameterRow(4, "Fixed", 3.0, 3.0, 3.0, 0);
		assertEquals("0 3.0", fixed.position(3.0) + " " + fixed.plain(700));
	}

	// a value typed is taken inside the range, either way round, and nothing else is
	@Test
	public void aValueTypedIsTakenOnlyInsideTheRange() {
		assertEquals("4.5 16.0 0.0 null null null null null | 5.0 null", GAIN.parse("4.5") + " " + GAIN.parse(" 16 ") + " " + GAIN.parse("0") + " " + GAIN.parse("16.5") + " "
			+ GAIN.parse("-0.1") + " " + GAIN.parse("loud") + " " + GAIN.parse("NaN") + " " + GAIN.parse("") + " | " + FALLING.parse("5") + " " + FALLING.parse("11"));
	}

	@Test
	public void aRowShowsItsParameter() {
		assertEquals("1 Gain 2.0", GAIN.getId() + " " + GAIN.getTitle() + " " + GAIN.getPlain());
	}
}
