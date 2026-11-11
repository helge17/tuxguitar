package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how a tied note is made valid (TGTrackManager.fixInvalidTiedNote): it is tied to the first note of the
 * previous voice with the same pitch, by moving the tied note onto that note's string, else that note onto the
 * tied note's string (unless it is tied itself); a move needs the string free in its beat. Without such a note,
 * or when no move fits, the tie is removed.
 *
 * Standard tuning: string 1 is E4 (64), string 2 is B3 (59), string 3 is G3 (55). A scenario is
 * "previous beat | beat -> previous beat | beat": the first quarter of a 4/4 measure, then the second, before and
 * after fixing the first note of the second beat. A beat lists its notes as "s<string>f<fret>", "~" when tied.
 */
public class TestFixInvalidTiedNote {

	private MeasureFixture fixture;

	// the notes of the given description, added in voice 0 of the beat at "offset"; returns the first one
	private TGNote beat(long offset, String description) {
		List<TGNote> notes = new ArrayList<TGNote>();
		for (String spec : description.trim().split(" ")) {
			String[] stringAndFret = spec.replace("~", "").substring(1).split("f");
			TGNote note = fixture.fretted(offset, Integer.parseInt(stringAndFret[0]), Integer.parseInt(stringAndFret[1]));
			note.setTiedNote(spec.endsWith("~"));
			notes.add(note);
		}
		return notes.get(0);
	}

	private static String describe(TGVoice voice) {
		List<String> notes = new ArrayList<String>();
		for (TGNote note : voice.getNotes()) {
			notes.add("s" + note.getString() + "f" + note.getValue() + (note.isTiedNote() ? "~" : ""));
		}
		return String.join(" ", notes);
	}

	// runs each scenario on a new measure, and checks the beats it ends with
	private void assertFixes(String... scenarios) {
		List<String> results = new ArrayList<String>();
		for (String scenario : scenarios) {
			fixture = new MeasureFixture();
			String[] beats = scenario.split(" -> ")[0].split("\\|");
			beat(0, beats[0]);
			fixture.songManager.getTrackManager().fixInvalidTiedNote(beat(QUARTER, beats[1]));
			results.add(scenario.split(" -> ")[0] + " -> " + describe(fixture.beatAt(0).getVoice(0)) + " | " + describe(fixture.beatAt(QUARTER).getVoice(0)));
		}
		assertEquals(String.join("\n", scenarios), String.join("\n", results));
	}

	@Test
	public void aTiedNoteIsTiedToTheFirstPreviousNoteOfItsPitchElseLosesItsTie() {
		assertFixes(
			// a note not tied is left alone
			"s2f5 | s1f0 -> s2f5 | s1f0",
			// the tied note moves onto the string of the previous note with its pitch, the first one
			"s2f5 | s1f0~ -> s2f5 | s2f5~",
			"s1f0 s2f5 | s3f9~ -> s1f0 s2f5 | s1f0~",
			// else the previous note moves onto the tied note's string, unless it is tied itself
			"s2f5 | s1f0~ s2f1 -> s1f0 | s1f0~ s2f1",
			"s2f5~ | s1f0~ s2f1 -> s2f5~ | s1f0 s2f1",
			// the tie is removed when neither note can move, or without a previous note of its pitch
			"s2f5 s1f3 | s1f0~ s2f1 -> s2f5 s1f3 | s1f0 s2f1",
			"s2f4 | s1f0~ -> s2f4 | s1f0");
	}

	@Test
	public void theTieIsRemovedWithoutAPreviousVoice() {
		fixture = new MeasureFixture();
		TGNote note = beat(0, "s1f0~");
		fixture.songManager.getTrackManager().fixInvalidTiedNote(note);

		assertEquals("s1f0", describe(fixture.beatAt(0).getVoice(0)));
	}
}
