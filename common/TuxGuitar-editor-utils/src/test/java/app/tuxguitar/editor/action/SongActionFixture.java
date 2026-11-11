package app.tuxguitar.editor.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

/**
 * A new song (one guitar track in standard tuning, one measure) and an action context on it, to run editor actions
 * the way the app does: mapped in the action manager and executed by name.
 *
 * A beat reads back as its voice 0 notes, lowest string number first: "s<string>f<fret> ...".
 */
public class SongActionFixture {

	public final TGContext context = new TGContext();
	public final TGSongManager songManager = new TGSongManager();
	public final TGSong song = songManager.newSong();
	public final TGActionContext actionContext = new TGActionContext() {
	};

	public SongActionFixture() {
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, songManager);
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
	}

	public void execute(TGActionBase action) {
		TGActionManager actionManager = TGActionManager.getInstance(context);
		actionManager.mapAction(action.getName(), action);
		actionManager.execute(action.getName(), actionContext);
	}

	public void select(TGNote... notes) {
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, new TGNoteRange(new ArrayList<TGNote>(Arrays.asList(notes))));
	}

	public void selectBeats(TGBeat... beats) {
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, new TGBeatRange(new ArrayList<TGBeat>(Arrays.asList(beats))));
	}

	// the caret on the given string of the beat, in voice 0
	public void caret(TGBeat beat, int string) {
		TGTrack track = beat.getMeasure().getTrack();
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, beat.getMeasure());
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(string));
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, beat.getVoice(0));
	}

	// the beats, separated by " | ", then "success" when the action reported it
	public String outcome(TGBeat... beats) {
		List<String> descriptions = new ArrayList<String>();
		for (TGBeat beat : beats) {
			descriptions.add(describe(beat));
		}
		if (Boolean.TRUE.equals(actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS))) {
			descriptions.add("success");
		}
		return String.join(" | ", descriptions);
	}

	// a new quarter beat in voice 0, the given number of quarters after the measure start
	public TGBeat beat(TGMeasure measure, int quarter) {
		TGBeat beat = songManager.getFactory().newBeat();
		beat.setPreciseStart(TGDuration.toPreciseTime(measure.getStart() + quarter * TGDuration.QUARTER_TIME));
		TGVoice voice = beat.getVoice(0);
		voice.setEmpty(false);
		voice.getDuration().setValue(TGDuration.QUARTER);
		songManager.getMeasureManager().addBeat(measure, beat);
		return beat;
	}

	public TGNote note(TGBeat beat, int string, int fret) {
		TGNote note = songManager.getFactory().newNote();
		note.setString(string);
		note.setValue(fret);
		beat.getVoice(0).addNote(note);
		return note;
	}

	public static String describe(TGBeat beat) {
		return describe(beat, note -> "");
	}

	// each note read as "s<string>f<fret>", followed by what the detail function reads in it
	public static String describe(TGBeat beat, Function<TGNote, String> detail) {
		List<TGNote> notes = new ArrayList<TGNote>(beat.getVoice(0).getNotes());
		notes.sort(Comparator.comparingInt(TGNote::getString));
		List<String> descriptions = new ArrayList<String>();
		for (TGNote note : notes) {
			descriptions.add("s" + note.getString() + "f" + note.getValue() + detail.apply(note));
		}
		return String.join(" ", descriptions);
	}
}
