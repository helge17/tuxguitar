package app.tuxguitar.editor.action.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGPasteMeasureAction: the measures copied (by TGCopyMeasureAction), repeated the paste count of times, either
 * replace the measures from the caret measure on (adding measures at the song end when needed) or are inserted before
 * it. Measures copied from one track go to the caret track, else each to its own track.
 *
 * The song has two tracks of three measures; measure m holds one beat with one note, of fret m on track 1 and of fret
 * 10 + m on track 2. A track reads back as its measures separated by " | ", each as its beats, and the tracks are
 * separated by " / ".
 */
public class TestPasteMeasureAction {

	private static final String UNCHANGED = "s1f1 | s1f2 | s1f3 / s1f11 | s1f12 | s1f13";
	// measure 1 of track 1 pasted over its measure 2
	private static final String FIRST_OVER_SECOND = "s1f1 | s1f1 | s1f3 / s1f11 | s1f12 | s1f13";

	private final SongActionFixture fixture = new SongActionFixture();

	public TestPasteMeasureAction() {
		fixture.songManager.addTrack(fixture.song);
		fixture.songManager.addNewMeasureBeforeEnd(fixture.song);
		fixture.songManager.addNewMeasureBeforeEnd(fixture.song);
		for (int t = 0; t < 2; t++) {
			for (int m = 0; m < 3; m++) {
				fixture.note(fixture.beat(fixture.song.getTrack(t).getMeasure(m), 0), 1, (10 * t) + m + 1);
			}
		}
	}

	private TGTrack track(int number) {
		return fixture.song.getTrack(number - 1);
	}

	// copies the measures, of the track or of every track, before the paste that follows
	private TestPasteMeasureAction copy(int track, int measure1, int measure2, boolean allTracks) {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track(track));
		fixture.actionContext.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		fixture.actionContext.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		fixture.actionContext.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, allTracks);
		fixture.execute(new TGCopyMeasureAction(fixture.context));
		return this;
	}

	private String paste(int mode, int count, int track, int measure) {
		fixture.actionContext.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_MODE, mode);
		fixture.actionContext.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_COUNT, count);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track(track));
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, track(track).getMeasure(measure - 1));
		fixture.execute(new TGPasteMeasureAction(fixture.context));
		return describe(track(1)) + " / " + describe(track(2));
	}

	private String replace(int count, int track, int measure) {
		return paste(TGPasteMeasureAction.TRANSFER_TYPE_REPLACE, count, track, measure);
	}

	private String insert(int count, int track, int measure) {
		return paste(TGPasteMeasureAction.TRANSFER_TYPE_INSERT, count, track, measure);
	}

	private static String describe(TGTrack track) {
		List<String> measures = new ArrayList<String>();
		for (int m = 0; m < track.countMeasures(); m++) {
			TGMeasure measure = track.getMeasure(m);
			List<String> beats = new ArrayList<String>();
			for (TGBeat beat : measure.getBeats()) {
				beats.add(SongActionFixture.describe(beat));
			}
			measures.add(String.join(" ", beats));
		}
		return String.join(" | ", measures);
	}

	@TestFactory
	public List<DynamicTest> pastingTheCopiedMeasures() {
		return Arrays.asList(
			pasting("replacing overwrites the caret measure", FIRST_OVER_SECOND,
					test -> test.copy(1, 1, 1, false).replace(1, 1, 2)),
			pasting("replacing past the song end adds measures", "s1f1 | s1f2 | s1f1 | s1f2 / s1f11 | s1f12 | s1f13 | ",
					test -> test.copy(1, 1, 2, false).replace(1, 1, 3)),
			pasting("inserting pushes the caret measure back", "s1f1 | s1f3 | s1f2 | s1f3 / s1f11 |  | s1f12 | s1f13",
					test -> test.copy(1, 3, 3, false).insert(1, 1, 2)),
			pasting("the count repeats the measures", "s1f1 | s1f1 | s1f1 | s1f2 | s1f3 / s1f11 |  |  | s1f12 | s1f13",
					test -> test.copy(1, 1, 1, false).insert(2, 1, 2)),
			pasting("measures of one track go to the caret track", "s1f1 | s1f2 | s1f3 / s1f11 | s1f1 | s1f13",
					test -> test.copy(1, 1, 1, false).replace(1, 2, 2)),
			pasting("measures of every track go to their own tracks", "s1f1 | s1f2 | s1f1 / s1f11 | s1f12 | s1f11",
					test -> test.copy(2, 1, 1, true).replace(1, 2, 3)));
	}

	// a case run on a new song: copying then pasting gives the song read back
	private static DynamicTest pasting(String name, String expected, Function<TestPasteMeasureAction, String> copyThenPaste) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, copyThenPaste.apply(new TestPasteMeasureAction())));
	}

	@Test
	public void pastingNeedsACountAndAKnownMode() {
		copy(1, 1, 1, false);

		assertEquals(UNCHANGED, replace(0, 1, 2));
		assertEquals(UNCHANGED, paste(0, 1, 1, 2));
		assertEquals(UNCHANGED, paste(3, 1, 1, 2));
		assertEquals(FIRST_OVER_SECOND, replace(1, 1, 2));
	}

	@Test
	public void pastingNeedsCopiedMeasures() {
		assertEquals(UNCHANGED, replace(1, 1, 2));

		TGSongSegment noTracks = new TGSongSegment();
		noTracks.getHeaders().add(fixture.song.getMeasureHeader(0));
		TGClipboard.getInstance(fixture.context).setData(noTracks);
		assertEquals(UNCHANGED, replace(1, 1, 2));

		copy(1, 1, 1, false);
		assertEquals(FIRST_OVER_SECOND, replace(1, 1, 2));
	}
}
