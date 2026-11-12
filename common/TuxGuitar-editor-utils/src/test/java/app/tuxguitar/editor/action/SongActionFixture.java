package app.tuxguitar.editor.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DynamicTest;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.TGUndoableManager;
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
 * the way the app does: mapped in the action manager and executed by name. Undoable edits read the song of the
 * document: for them the fixture is made on it, and an action can be executed with its undoable edit kept for undo
 * and redo, which run on a new action context, as in the app.
 *
 * A beat reads back as its voice 0 notes, lowest string number first: "s<string>f<fret> ...".
 */
public class SongActionFixture {

	public final TGContext context;
	public final TGSongManager songManager;
	public final TGSong song;
	public final TGActionContext actionContext;

	public SongActionFixture() {
		this(new TGContext(), new TGSongManager());
	}

	private SongActionFixture(TGContext context, TGSongManager songManager) {
		this(context, songManager, songManager.newSong());
	}

	private SongActionFixture(TGContext context, TGSongManager songManager, TGSong song) {
		this.context = context;
		this.songManager = songManager;
		this.song = song;
		this.actionContext = newActionContext();
	}

	// a fixture on the song of the document of a new context
	public static SongActionFixture onDocument() {
		TGContext context = new TGContext();
		TGDocumentManager document = TGDocumentManager.getInstance(context);
		return new SongActionFixture(context, document.getSongManager(), document.getSong());
	}

	private TGActionContext newActionContext() {
		TGActionContext newContext = new TGActionContext() {
		};
		newContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, songManager);
		newContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		return newContext;
	}

	public void execute(TGActionBase action) {
		execute(action, actionContext);
	}

	private void execute(TGActionBase action, TGActionContext onContext) {
		map(action);
		TGActionManager.getInstance(context).execute(action.getName(), onContext);
	}

	// executes the action the way the app's undoable listener does: the controller starts the undoable edit before
	// the action and ends it after, and the undo manager keeps it; returns the edit
	public TGUndoableEdit executeUndoable(TGActionBase action, TGUndoableActionController controller) {
		TGUndoableEdit started = controller.startUndoable(context, actionContext);
		execute(action);
		TGUndoableEdit edit = controller.endUndoable(context, actionContext, started);
		TGUndoableManager.getInstance(context).addEdit(edit);
		return edit;
	}

	public void undo() {
		execute(new TGUndoAction(context), newActionContext());
	}

	public void redo() {
		execute(new TGRedoAction(context), newActionContext());
	}

	// maps actions that the executed action runs in turn
	public void map(TGActionBase... actions) {
		for (TGActionBase action : actions) {
			TGActionManager.getInstance(context).mapAction(action.getName(), action);
		}
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
		List<String> descriptions = Arrays.stream(beats).map(SongActionFixture::describe).collect(Collectors.toCollection(ArrayList::new));
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

	// adds measures at the song end, so the song has the given number of measures
	public void measureCount(int count) {
		while (song.countMeasureHeaders() < count) {
			songManager.addNewMeasureBeforeEnd(song);
		}
	}

	// the measures of the first track, each read by the given function, separated by " | "
	public String measures(Function<TGMeasure, String> reading) {
		TGTrack track = song.getTrack(0);
		List<String> descriptions = new ArrayList<String>();
		for (int m = 0; m < track.countMeasures(); m++) {
			descriptions.add(reading.apply(track.getMeasure(m)));
		}
		return String.join(" | ", descriptions);
	}

	public TGNote note(TGBeat beat, int string, int fret) {
		TGNote note = songManager.getFactory().newNote();
		note.setString(string);
		note.setValue(fret);
		beat.getVoice(0).addNote(note);
		return note;
	}

	// a table case: what the run reads back, on its own new song, is the expected
	public static DynamicTest reads(String name, String expected, Supplier<String> run) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, run.get()));
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
