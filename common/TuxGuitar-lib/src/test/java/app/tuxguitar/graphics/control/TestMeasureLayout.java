package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.CHORDS;
import static app.tuxguitar.graphics.control.LayoutHarness.COMPACT;
import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;

/**
 * Pins how a measure lays out its beats (TGMeasureImpl.create / update): measure width, quarter
 * spacing, beat positions and widths, beam groups, hidden rests, and the beat's lowest/highest notes.
 *
 * A measure is described as a header line, then one line per beat:
 * "x=posX w=width min/max=strings v<voice>=<n|r|h>/g<group>" (h = hidden rest, group in order of appearance).
 */
public class TestMeasureLayout {

	private final LayoutHarness harness = new LayoutHarness();

	@Test
	public void anEmptyMeasureIsCompletedWithAWholeRest() {
		harness.update(TAB);

		assertEquals(""
			+ "width=100.0 quarter=6.25 effect=0.0 notEmptyBeats=0 notEmptyVoices=1\n"
			+ "x=45.0 w=25.0 v0=r/g-\n"
			, describe());
	}

	@Test
	public void quarterNotesAreSpacedByTheShortestDurationExceptInCompactMode() {
		harness.quarterNotes(0, 1, 2, 3);
		harness.update(TAB);
		String normal = describe();
		harness.update(TAB | COMPACT);

		// same layout, but compact mode leaves the quarter spacing unset
		assertEquals(normal.replace("quarter=20.0", "quarter=0.0"), describe());
		assertEquals(""
			+ "width=155.0 quarter=20.0 effect=0.0 notEmptyBeats=4 notEmptyVoices=1\n"
			+ "x=45.0 w=20.0 min/max=3/3 v0=n/g0\n"
			+ "x=65.0 w=20.0 min/max=3/3 v0=n/g1\n"
			+ "x=85.0 w=20.0 min/max=3/3 v0=n/g2\n"
			+ "x=105.0 w=20.0 min/max=3/3 v0=n/g3\n"
			, normal);
	}

	@Test
	public void eighthsInTheSameQuarterShareABeamGroup() {
		eighthsThenHalf();
		harness.update(SCORE | TAB);

		assertEquals(""
			+ "width=255.0 quarter=36.0 effect=0.0 notEmptyBeats=5 notEmptyVoices=1\n"
			+ "x=81.0 w=18.0 min/max=2/2 v0=n/g0\n"
			+ "x=99.0 w=18.0 min/max=2/2 v0=n/g0\n"
			+ "x=117.0 w=18.0 min/max=2/2 v0=n/g1\n"
			+ "x=135.0 w=18.0 min/max=2/2 v0=n/g1\n"
			+ "x=153.0 w=72.0 min/max=4/4 v0=n/g2\n"
			, describe());
	}

	@Test
	public void restsTakeTheBeamGroupOfTheirVoiceSoFar() {
		harness.note(0, TGDuration.EIGHTH, 0, 3, 5);
		harness.rest(QUARTER / 2, TGDuration.EIGHTH, 0);
		harness.note(QUARTER, TGDuration.EIGHTH, 0, 3, 5);
		harness.update(TAB);

		assertEquals(""
			+ "width=219.0 quarter=36.0 effect=0.0 notEmptyBeats=2 notEmptyVoices=1\n"
			+ "x=45.0 w=18.0 min/max=3/3 v0=n/g0\n"
			+ "x=63.0 w=18.0 v0=r/g0\n"
			+ "x=81.0 w=18.0 min/max=3/3 v0=n/g1\n"
			+ "x=99.0 w=18.0 v0=r/g1\n"
			+ "x=117.0 w=18.0 v0=r/g1\n"
			+ "x=135.0 w=18.0 v0=r/g1\n"
			+ "x=153.0 w=18.0 v0=r/g1\n"
			+ "x=171.0 w=18.0 v0=r/g1\n"
			, describe());
	}

	@Test
	public void aRestUnderANoteOfTheSameDurationIsHidden() {
		harness.note(0, TGDuration.HALF, 0, 3, 5);
		harness.rest(0, TGDuration.HALF, 1);
		harness.note(2 * QUARTER, TGDuration.HALF, 0, 1, 5);
		harness.note(2 * QUARTER, TGDuration.HALF, 0, 6, 7);
		harness.rest(2 * QUARTER, TGDuration.QUARTER, 1);
		harness.update(SCORE | TAB);

		assertEquals(""
			+ "width=191.0 quarter=20.0 effect=0.0 notEmptyBeats=2 notEmptyVoices=2\n"
			+ "x=81.0 w=40.0 min/max=3/3 v0=n/g0 v1=h/g-\n"
			+ "x=121.0 w=20.0 min/max=1/6 v0=n/g1 v1=r/g-\n"
			+ "x=141.0 w=20.0 v1=r/g-\n"
			, describe());
	}

	@Test
	public void ofTwoEqualRestsOnlyTheFirstIsHidden() {
		harness.rest(0, TGDuration.HALF, 0);
		harness.rest(0, TGDuration.HALF, 1);
		harness.quarterNotes(2, 3);
		harness.update(TAB);

		assertEquals(""
			+ "width=155.0 quarter=20.0 effect=0.0 notEmptyBeats=2 notEmptyVoices=2\n"
			+ "x=45.0 w=40.0 v0=h/g- v1=r/g-\n"
			+ "x=85.0 w=20.0 min/max=3/3 v0=n/g0 v1=r/g-\n"
			+ "x=105.0 w=20.0 min/max=3/3 v0=n/g1\n"
			, describe());
	}

	@Test
	public void beatsAreLinkedInOrder() {
		harness.quarterNotes(0, 1, 2, 3);
		harness.update(TAB);

		TGBeatImpl first = (TGBeatImpl) harness.measure().getBeat(0);
		TGBeatImpl second = (TGBeatImpl) harness.measure().getBeat(1);
		assertSame(second, first.getNextBeat());
		assertSame(first, second.getPreviousBeat());
	}

	@Test
	public void closeChordsWidenTheQuarterSpacingOrInCompactModeTheFirstChordBeat() {
		harness.quarterNotes(0, 1, 2, 3);
		chord(0);
		chord(1);
		StringBuilder layouts = new StringBuilder();
		for (int style : new int[] {TAB | CHORDS, TAB | CHORDS | COMPACT, TAB}) {
			harness.update(style);
			layouts.append(describe());
		}

		assertEquals(""
			// chords shown
			+ "width=219.0 quarter=36.0 effect=0.0 notEmptyBeats=4 notEmptyVoices=1\n"
			+ "x=45.0 w=36.0 min/max=3/3 v0=n/g0\n"
			+ "x=81.0 w=36.0 min/max=3/3 v0=n/g1\n"
			+ "x=117.0 w=36.0 min/max=3/3 v0=n/g2\n"
			+ "x=153.0 w=36.0 min/max=3/3 v0=n/g3\n"
			// chords shown, compact mode
			+ "width=171.0 quarter=0.0 effect=0.0 notEmptyBeats=4 notEmptyVoices=1\n"
			+ "x=45.0 w=36.0 min/max=3/3 v0=n/g0\n"
			+ "x=81.0 w=20.0 min/max=3/3 v0=n/g1\n"
			+ "x=101.0 w=20.0 min/max=3/3 v0=n/g2\n"
			+ "x=121.0 w=20.0 min/max=3/3 v0=n/g3\n"
			// chords hidden: ignored
			+ "width=155.0 quarter=20.0 effect=0.0 notEmptyBeats=4 notEmptyVoices=1\n"
			+ "x=45.0 w=20.0 min/max=3/3 v0=n/g0\n"
			+ "x=65.0 w=20.0 min/max=3/3 v0=n/g1\n"
			+ "x=85.0 w=20.0 min/max=3/3 v0=n/g2\n"
			+ "x=105.0 w=20.0 min/max=3/3 v0=n/g3\n"
			, layouts.toString());
	}

	@Test
	public void aBendWidensItsBeatAndTheMeasure() {
		harness.quarterNotes(0, 2, 3);
		harness.bend(harness.note(QUARTER, TGDuration.QUARTER, 0, 3, 5), 0, 4);
		harness.update(TAB);

		assertEquals(""
			+ "width=165.0 quarter=20.0 effect=10.0 notEmptyBeats=4 notEmptyVoices=1\n"
			+ "x=45.0 w=20.0 min/max=3/3 v0=n/g0\n"
			+ "x=65.0 w=30.0 min/max=3/3 v0=n/g1\n"
			+ "x=95.0 w=20.0 min/max=3/3 v0=n/g2\n"
			+ "x=115.0 w=20.0 min/max=3/3 v0=n/g3\n"
			, describe());
	}

	@Test
	public void aBeatWithNoVoiceIsFilledWithARest() {
		harness.quarterNotes(0, 2, 3);
		harness.beatAt(QUARTER);
		harness.update(TAB);

		assertEquals(""
			+ "width=155.0 quarter=20.0 effect=0.0 notEmptyBeats=3 notEmptyVoices=1\n"
			+ "x=45.0 w=20.0 min/max=3/3 v0=n/g0\n"
			+ "x=65.0 w=20.0 v0=r/g0\n"
			+ "x=85.0 w=20.0 min/max=3/3 v0=n/g1\n"
			+ "x=105.0 w=20.0 min/max=3/3 v0=n/g2\n"
			, describe());
	}

	// ---- helpers ----

	private void eighthsThenHalf() {
		for (int i = 0; i < 4; i++) {
			harness.note(i * (QUARTER / 2), TGDuration.EIGHTH, 0, 2, i);
		}
		harness.note(2 * QUARTER, TGDuration.HALF, 0, 4, 2);
	}

	private void chord(int quarter) {
		TGChord chord = harness.factory.newChord(6);
		chord.setName("C");
		harness.beatAt(quarter * QUARTER).setChord(chord);
	}

	private String describe() {
		TGMeasureImpl measure = harness.measure();
		TGLayout layout = harness.layout();
		StringBuilder text = new StringBuilder();
		text.append("width=").append(measure.getWidth(layout))
			.append(" quarter=").append(measure.getHeaderImpl().getMaxQuarterSpacing())
			.append(" effect=").append(measure.getHeaderImpl().getMaxEffectWidth())
			.append(" notEmptyBeats=").append(measure.getNotEmptyBeats())
			.append(" notEmptyVoices=").append(measure.getNotEmptyVoices())
			.append('\n');
		List<TGBeatGroup> groups = new ArrayList<TGBeatGroup>();
		for (TGBeat beat : measure.getBeats()) {
			TGBeatImpl impl = (TGBeatImpl) beat;
			text.append("x=").append(impl.getPosX()).append(" w=").append(impl.getMinimumWidth());
			if (impl.getMinNote() != null) {
				text.append(" min/max=").append(impl.getMinNote().getString()).append('/').append(impl.getMaxNote().getString());
			}
			for (int v = 0; v < beat.countVoices(); v++) {
				TGVoiceImpl voice = impl.getVoiceImpl(v);
				if (!voice.isEmpty()) {
					text.append(" v").append(v).append('=').append(kind(voice)).append("/g").append(groupIndex(groups, voice.getBeatGroup()));
				}
			}
			text.append('\n');
		}
		return text.toString();
	}

	private static String kind(TGVoiceImpl voice) {
		if (!voice.isRestVoice()) {
			return "n";
		}
		return voice.isHiddenSilence() ? "h" : "r";
	}

	private static String groupIndex(List<TGBeatGroup> groups, TGBeatGroup group) {
		if (group == null) {
			return "-";
		}
		if (!groups.contains(group)) {
			groups.add(group);
		}
		return String.valueOf(groups.indexOf(group));
	}
}
