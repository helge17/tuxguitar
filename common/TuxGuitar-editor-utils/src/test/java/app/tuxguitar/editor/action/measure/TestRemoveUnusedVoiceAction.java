package app.tuxguitar.editor.action.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

/**
 * Pins which voice "remove unused voice" removes (TGRemoveUnusedVoiceAction): the last voice that holds
 * no note in any selected beat (or in the current beat, without a selection), removed from every measure
 * of those beats, unless that measure has notes in the voice elsewhere.
 *
 * A beat is written as one character per voice: n = notes, r = rest, - = empty. Measures are separated by " | ".
 */
public class TestRemoveUnusedVoiceAction {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGActionContext context = new TGActionContext() {
	};

	public TestRemoveUnusedVoiceAction() {
		songManager.addNewMeasureBeforeEnd(song);
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, songManager);
	}

	@Test
	public void aVoiceOfRestsIsRemoved() {
		select(beat(0, "nr"), beat(0, "nr"));

		assertEquals("n- n- | ", removeUnusedVoice());
	}

	@Test
	public void ofTwoUnusedVoicesTheLastIsRemoved() {
		select(beat(0, "rr"));

		assertEquals("r- | ", removeUnusedVoice());
	}

	@Test
	public void theFirstVoiceIsRemovedWhenItIsTheOnlyUnusedOne() {
		select(beat(0, "rn"), beat(0, "-n"));

		assertEquals("-n -n | ", removeUnusedVoice());
	}

	@Test
	public void voicesWithNotesInAnySelectedBeatAreKept() {
		select(beat(0, "nr"), beat(0, "rn"));

		assertEquals("nr rn | ", removeUnusedVoice());
	}

	@Test
	public void theVoiceIsRemovedFromEveryMeasureOfTheSelection() {
		select(beat(0, "nr"), beat(1, "nr"), beat(1, "nr"));

		assertEquals("n- | n- n-", removeUnusedVoice());
	}

	@Test
	public void aMeasureWithNotesInTheVoiceOutsideTheSelectionKeepsIt() {
		select(beat(0, "nr"));
		beat(0, "nn");

		assertEquals("nr nn | ", removeUnusedVoice());
	}

	@Test
	public void withoutASelectionTheCurrentBeatDecides() {
		TGBeat current = beat(0, "nr");
		beat(1, "nr");
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, current);

		assertEquals("n- | nr", removeUnusedVoice());
	}

	@Test
	public void anEmptySelectionFallsBackToTheCurrentBeat() {
		TGBeat current = beat(0, "nn");
		beat(1, "nr");
		select();
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, current);

		assertEquals("nn | nr", removeUnusedVoice());
	}

	// ---- helpers ----

	private void select(TGBeat... beats) {
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, new TGBeatRange(new ArrayList<TGBeat>(Arrays.asList(beats))));
	}

	// a new beat appended to the measure, its voices as described
	private TGBeat beat(int measureIndex, String voices) {
		TGMeasure measure = song.getTrack(0).getMeasure(measureIndex);
		TGBeat beat = songManager.getFactory().newBeat();
		beat.setStart(measure.getStart() + measure.countBeats());
		for (int v = 0; v < voices.length(); v++) {
			char kind = voices.charAt(v);
			TGVoice voice = beat.getVoice(v);
			voice.setEmpty(kind == '-');
			if (kind == 'n') {
				TGNote note = songManager.getFactory().newNote();
				note.setString(1);
				note.setValue(5);
				voice.addNote(note);
			}
		}
		songManager.getMeasureManager().addBeat(measure, beat);
		return beat;
	}

	private String removeUnusedVoice() {
		new TGRemoveUnusedVoiceAction(new TGContext()).processAction(context);
		List<String> measures = new ArrayList<String>();
		for (int m = 0; m < song.countMeasureHeaders(); m++) {
			List<String> beats = new ArrayList<String>();
			for (TGBeat beat : song.getTrack(0).getMeasure(m).getBeats()) {
				beats.add(describe(beat));
			}
			measures.add(String.join(" ", beats));
		}
		return String.join(" | ", measures);
	}

	private static String describe(TGBeat beat) {
		StringBuilder voices = new StringBuilder();
		for (int v = 0; v < TGBeat.MAX_VOICES; v++) {
			TGVoice voice = beat.getVoice(v);
			voices.append(voice.isEmpty() ? '-' : voice.isRestVoice() ? 'r' : 'n');
		}
		return voices.toString();
	}
}
