package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;

/**
 * Pins the precise beat ranges of TGMeasureManager: getBeatsBeforeEndPrecise keeps the beats from a start on,
 * getBeatsBeetweenPrecise those from a start up to (not including) an end, and removeBeatsBetweenPrecise removes the
 * latter from the measure.
 *
 * The measure holds quarter notes at positions 0 to 3; a result is the positions kept, in quarters.
 */
public class TestBeatsInRange {

	private final MeasureFixture fixture = new MeasureFixture();

	public TestBeatsInRange() {
		fixture.quarterNotes(0, 1, 2, 3);
	}

	private String positions(List<TGBeat> beats) {
		StringBuilder positions = new StringBuilder();
		for (TGBeat beat : beats) {
			positions.append(positions.length() == 0 ? "" : " ").append((beat.getPreciseStart() - fixture.at(0)) / QUARTER);
		}
		return positions.toString();
	}

	private String from(int quarter) {
		return positions(fixture.manager.getBeatsBeforeEndPrecise(fixture.measure.getBeats(), fixture.at(quarter * QUARTER)));
	}

	private String between(int fromQuarter, int toQuarter) {
		return positions(fixture.manager.getBeatsBeetweenPrecise(fixture.measure.getBeats(), fixture.at(fromQuarter * QUARTER), fixture.at(toQuarter * QUARTER)));
	}

	@Test
	public void fromAStartOnIncludesTheBeatAtTheStart() {
		assertEquals("0 1 2 3", from(0));
		assertEquals("2 3", from(2));
		assertEquals("", from(4));
	}

	@Test
	public void betweenIncludesTheStartButNotTheEnd() {
		assertEquals("1 2", between(1, 3));
		assertEquals("0 1 2 3", between(0, 4));
		assertEquals("", between(2, 2));
	}

	@Test
	public void removingARangeLeavesTheBeatsAround() {
		fixture.manager.removeBeatsBetweenPrecise(fixture.measure, fixture.at(QUARTER), fixture.at(3 * QUARTER));
		assertEquals("n@0 n@3", fixture.layout());
	}
}
