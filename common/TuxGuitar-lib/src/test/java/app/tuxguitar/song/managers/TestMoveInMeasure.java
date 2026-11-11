package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;

/**
 * Pins TGMeasureManager.moveBeatsInMeasurePrecise (whole beats) and moveVoicesPrecise (voice 0):
 * everything from a position on is shifted; rests pushed out of the measure are dropped, other components
 * pushed out make the move fail and leave the measure as it was; the gap left behind can be filled with a rest.
 * Layouts are described in MeasureFixture.
 */
public class TestMoveInMeasure {

	private static final int NO_FILL = 0;

	private enum Mover {
		BEATS, VOICES;

		boolean move(MeasureFixture f, long offset, long move, int fillValue) {
			TGDuration fill = (fillValue == NO_FILL ? null : f.duration(fillValue));
			if (this == BEATS) {
				return f.manager.moveBeatsInMeasurePrecise(f.measure, f.at(offset), move, fill);
			}
			return f.manager.moveVoicesPrecise(f.measure, f.at(offset), move, 0, fill);
		}
	}

	private final MeasureFixture f = new MeasureFixture();

	// --- beats and voices alike --------------------------------------------------------------

	// a measure set up by "arrange", moved from "offset" by "move"; the move's result and the layout after it
	private static final class Case {
		final String name;
		final Consumer<MeasureFixture> arrange;
		final long offset;
		final long move;
		final int fillValue;
		final boolean moved;
		final String layout;

		Case(String name, Consumer<MeasureFixture> arrange, long offset, long move, int fillValue, boolean moved, String layout) {
			this.name = name;
			this.arrange = arrange;
			this.offset = offset;
			this.move = move;
			this.fillValue = fillValue;
			this.moved = moved;
			this.layout = layout;
		}

		void check(Mover mover) {
			MeasureFixture f = new MeasureFixture();
			this.arrange.accept(f);
			assertEquals(this.moved, mover.move(f, this.offset, this.move, this.fillValue));
			assertEquals(this.layout, f.layout());
		}
	}

	private static final Consumer<MeasureFixture> NOTES_1_TO_3 = f -> f.quarterNotes(1, 2, 3);
	private static final Consumer<MeasureFixture> NOTES_0_TO_3 = f -> f.quarterNotes(0, 1, 2, 3);
	private static final Consumer<MeasureFixture> NOTES_0_TO_2_THEN_REST = f -> {
		f.quarterNotes(0, 1, 2);
		f.rest(3 * QUARTER, TGDuration.QUARTER, 0);
	};
	private static final Consumer<MeasureFixture> TEXT_REST_THEN_NOTES = f -> {
		f.textRest(0, TGDuration.QUARTER, 0);
		f.quarterNotes(1, 2, 3);
	};

	private static final Case[] BEATS_AND_VOICES_ALIKE = {
		new Case("not moved by zero", NOTES_0_TO_3, 0, 0, TGDuration.QUARTER, false, "n@0 n@1 n@2 n@3"),
		new Case("moved backward, a rest is left at the end", NOTES_1_TO_3, QUARTER, -QUARTER, TGDuration.QUARTER, true, "n@0 n@1 n@2 r@3"),
		new Case("moved backward, a rest too long for the end is skipped", NOTES_1_TO_3, QUARTER, -QUARTER, TGDuration.HALF, true, "n@0 n@1 n@2"),
		new Case("moved forward, trailing rests are dropped and a rest is left behind", NOTES_0_TO_2_THEN_REST, QUARTER, QUARTER, TGDuration.QUARTER, true, "n@0 r@1 n@2 n@3"),
		new Case("moved forward, a rest too long for the gap is skipped", NOTES_0_TO_2_THEN_REST, QUARTER, QUARTER, TGDuration.HALF, true, "n@0 n@2 n@3"),
		new Case("the only rest moved out is replaced from the start", f -> f.rest(0, TGDuration.QUARTER, 0), 0, -QUARTER, TGDuration.QUARTER, true, "r@0"),
		new Case("pushing a note past the measure is refused", NOTES_0_TO_3, QUARTER, QUARTER, TGDuration.QUARTER, false, "n@0 n@1 n@2 n@3"),
		new Case("pushing a note before the measure is refused", NOTES_0_TO_3, 0, -QUARTER, TGDuration.QUARTER, false, "n@0 n@1 n@2 n@3"),
		new Case("pushing the only voice of a text rest out is refused", TEXT_REST_THEN_NOTES, 0, -QUARTER, TGDuration.QUARTER, false, "t@0 n@1 n@2 n@3"),
	};

	@TestFactory
	public List<DynamicTest> beatsAndVoicesAlike() {
		List<DynamicTest> tests = new ArrayList<DynamicTest>();
		for (Case c : BEATS_AND_VOICES_ALIKE) {
			for (Mover mover : Mover.values()) {
				tests.add(DynamicTest.dynamicTest(mover + ": " + c.name, () -> c.check(mover)));
			}
		}
		return tests;
	}

	// --- whole beats -------------------------------------------------------------------------

	@Test
	public void restLeftAtTheEndFillsEveryVoice() {
		f.quarterNotes(1, 2, 3);

		Mover.BEATS.move(f, QUARTER, -QUARTER, TGDuration.QUARTER);
		TGBeat rest = f.manager.getBeatPrecise(f.measure, f.at(3 * QUARTER));
		for (int v = 0; v < rest.countVoices(); v++) {
			assertFalse(rest.getVoice(v).isEmpty());
			assertEquals(TGDuration.QUARTER, rest.getVoice(v).getDuration().getValue());
		}
	}

	@Test
	public void beatsMovedBackwardWithoutFillLeaveAGap() {
		f.quarterNotes(1, 2, 3);

		assertTrue(Mover.BEATS.move(f, QUARTER, -QUARTER, NO_FILL));
		assertEquals("n@0 n@1 n@2", f.layout());
	}

	@Test
	public void beatsMovedBackwardDropLeadingRests() {
		f.rest(0, TGDuration.QUARTER, 0);
		f.quarterNotes(1, 2, 3);

		assertTrue(Mover.BEATS.move(f, 0, -QUARTER, TGDuration.QUARTER));
		assertEquals("n@0 n@1 n@2 r@3", f.layout());
	}

	@Test
	public void beatsAreNotCheckedWhenTheLastBeatIsEmpty() {
		f.quarterNotes(0, 1, 2, 3);
		f.beatAt(3 * QUARTER + QUARTER / 2);

		assertTrue(Mover.BEATS.move(f, QUARTER, QUARTER, NO_FILL));
		assertEquals("n@0 n@2 n@3 n@4 _@4.5", f.layout());
	}

	// --- one voice ---------------------------------------------------------------------------

	@Test
	public void voicesRestLeftBehindReusesABeatOfAnotherVoice() {
		f.quarterNotes(0, 1, 2);
		f.note(QUARTER, TGDuration.QUARTER, 1);

		assertTrue(Mover.VOICES.move(f, QUARTER, QUARTER, TGDuration.QUARTER));
		assertEquals("n@0 r@1 n@2 n@3", f.layout());
		assertFalse(f.manager.getBeatPrecise(f.measure, f.at(QUARTER)).getVoice(1).isRestVoice());
	}

	@Test
	public void voicesMayLeaveTheMeasureInFreeEditionMode() {
		f.songManager.setFreeEditionMode(true);
		f.quarterNotes(0, 1, 2, 3);

		assertTrue(Mover.VOICES.move(f, QUARTER, QUARTER, NO_FILL));
		assertEquals("n@0 n@2 n@3 n@4", f.layout());
	}

	@Test
	public void voicesDropATextRestSharedWithAnotherVoice() {
		f.textRest(0, TGDuration.QUARTER, 0);
		f.note(0, TGDuration.QUARTER, 1);
		f.quarterNotes(1, 2, 3);

		assertTrue(Mover.VOICES.move(f, 0, -QUARTER, NO_FILL));
		assertEquals("n@0 n@1 n@2", f.layout());
		assertTrue(f.measure.getBeat(0).isTextBeat());
	}

	@Test
	public void voicesDropOnlyTheRestsPushedOut() {
		f.rest(0, TGDuration.QUARTER, 0);
		f.quarterNotes(1, 2);
		f.rest(3 * QUARTER, TGDuration.QUARTER, 0);

		assertTrue(Mover.VOICES.move(f, 0, -QUARTER, NO_FILL));
		assertEquals("n@0 n@1 r@2", f.layout());
	}
}
