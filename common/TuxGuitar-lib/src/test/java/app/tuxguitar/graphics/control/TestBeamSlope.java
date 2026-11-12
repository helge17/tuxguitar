package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins where a beam group puts the end of its stems (TGBeatGroup.getY2): the beam runs from the first to the last
 * note of the group, beyond the outermost notes (below the lowest for stems down, above the highest for stems up),
 * its slope limited to 10 units, and flat when a note inside the group is further out than both ends.
 *
 * A chord counts at its lowest note for stems down and at its highest for stems up; a repeated outermost note counts
 * at its first occurrence, so the beam slants from it rather than lying flat.
 *
 * Notes are on the high E string (with, for chords, a note on the B string); a group is described as "y@x" at each stem, then at the middle of the group.
 */
public class TestBeamSlope {

	private LayoutHarness harness;
	// per beat, a fret on the B string sounding with the high E note, or NONE
	private int[] chordFrets = new int[0];

	private static final int NONE = -1;

	// notes of the given value, on the given frets, in one beam group, with stems forced in one direction
	private String beam(int direction, int durationValue, int... frets) {
		harness = new LayoutHarness();
		long length = (TGDuration.WHOLE_PRECISE_DURATION / durationValue);
		for (int i = 0; i < frets.length; i++) {
			harness.note(i * length, durationValue, 0, 1, frets[i]).getVoice().setDirection(direction);
			if (i < chordFrets.length && chordFrets[i] != NONE) {
				harness.note(i * length, durationValue, 0, 2, chordFrets[i]);
			}
		}
		harness.update(SCORE);

		TGBeatGroup group = ((TGBeatImpl) harness.measure().getBeat(0)).getVoiceImpl(0).getBeatGroup();
		StringBuilder description = new StringBuilder();
		float first = 0;
		float last = 0;
		for (int i = 0; i < frets.length; i++) {
			TGBeatImpl beat = ((TGBeatImpl) harness.measure().getBeat(i));
			assertEquals(group, beat.getVoiceImpl(0).getBeatGroup(), "one beam group");
			float x = beat.getVoiceImpl(0).getPosX() + beat.getSpacing(harness.layout());
			first = (i == 0 ? x : first);
			last = x;
			description.append(describe(group, x)).append(' ');
		}
		return description.append(describe(group, (first + last) / 2f)).toString();
	}

	private String describe(TGBeatGroup group, float x) {
		return group.getY2(harness.layout(), x) + "@" + x;
	}

	@Test
	public void stemsDownFollowTheLowestEnd() {
		assertEquals("30.625@81.0 27.125@99.0 28.875@90.0", beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 0, 2));
		assertEquals("27.125@81.0 30.625@99.0 28.875@90.0", beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 2, 0));
	}

	@Test
	public void stemsDownSlopeAtMostTenUnits() {
		assertEquals("30.625@81.0 20.625@99.0 25.625@90.0", beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 0, 12));
		assertEquals("20.625@81.0 30.625@99.0 25.625@90.0", beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 12, 0));
	}

	@Test
	public void stemsDownStayFlatUnderALowerInnerNote() {
		assertEquals("30.625@81.0 30.625@97.0 30.625@113.0 30.625@129.0 30.625@105.0",
				beam(TGVoice.DIRECTION_DOWN, TGDuration.SIXTEENTH, 5, 0, 5, 5));
	}

	@Test
	public void stemsUpFollowTheHighestEnd() {
		assertEquals("-24.5@81.0 -28.0@99.0 -26.25@90.0", beam(TGVoice.DIRECTION_UP, TGDuration.EIGHTH, 0, 2));
		assertEquals("-28.0@81.0 -24.5@99.0 -26.25@90.0", beam(TGVoice.DIRECTION_UP, TGDuration.EIGHTH, 2, 0));
	}

	@Test
	public void stemsUpSlopeAtMostTenUnits() {
		assertEquals("-39.0@81.0 -49.0@99.0 -44.0@90.0", beam(TGVoice.DIRECTION_UP, TGDuration.EIGHTH, 0, 12));
		assertEquals("-49.0@81.0 -39.0@99.0 -44.0@90.0", beam(TGVoice.DIRECTION_UP, TGDuration.EIGHTH, 12, 0));
	}

	@Test
	public void stemsUpStayFlatOverAHigherInnerNote() {
		assertEquals("-49.0@81.0 -49.0@97.0 -49.0@113.0 -49.0@129.0 -49.0@105.0",
				beam(TGVoice.DIRECTION_UP, TGDuration.SIXTEENTH, 5, 12, 5, 5));
	}

	@Test
	public void aLoneNoteHasAFlatStemEnd() {
		assertEquals("30.625@81.0 30.625@81.0", beam(TGVoice.DIRECTION_DOWN, TGDuration.QUARTER, 0));
		assertEquals("-24.5@81.0 -24.5@81.0", beam(TGVoice.DIRECTION_UP, TGDuration.QUARTER, 0));
	}

	@Test
	public void evenNotesHaveAFlatBeam() {
		assertEquals("30.625@81.0 30.625@99.0 30.625@90.0", beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 0, 0));
	}

	@Test
	public void theBeamIsExtendedPastItsEnds() {
		beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 0, 2);
		TGBeatGroup group = ((TGBeatImpl) harness.measure().getBeat(0)).getVoiceImpl(0).getBeatGroup();
		assertEquals("39.958336@33.0", describe(group, 33f));
	}

	// ---- chords and repeated notes: which note the beam starts, ends, or lies flat at ----

	@Test
	public void stemsDownStartAndEndAtTheLowestNoteOfAChord() {
		chordFrets = new int[] {0, NONE};
		String chordFirst = beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 0, 2);
		chordFrets = new int[] {NONE, 0};
		String chordLast = beam(TGVoice.DIRECTION_DOWN, TGDuration.EIGHTH, 2, 0);

		assertEquals("41.125@81.0 31.125@99.0 36.125@90.0 | 31.125@81.0 41.125@99.0 36.125@90.0", chordFirst + " | " + chordLast);
	}

	@Test
	public void stemsUpEndAtTheHighestNoteOfAChord() {
		chordFrets = new int[] {NONE, 0};

		assertEquals("-24.5@81.0 -24.5@99.0 -24.5@90.0", beam(TGVoice.DIRECTION_UP, TGDuration.EIGHTH, 0, 0));
	}

	@Test
	public void aRepeatedOutermostNoteCountsAtItsFirstOccurrence() {
		assertEquals("30.625@81.0 25.625@97.0 20.625@113.0 25.625@97.0 | -35.0@81.0 -30.0@97.0 -25.0@113.0 -30.0@97.0",
			beam(TGVoice.DIRECTION_DOWN, TGDuration.SIXTEENTH, 0, 0, 5)
			+ " | " + beam(TGVoice.DIRECTION_UP, TGDuration.SIXTEENTH, 5, 5, 0));
	}
}
