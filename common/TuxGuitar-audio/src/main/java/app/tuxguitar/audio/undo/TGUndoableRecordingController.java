package app.tuxguitar.audio.undo;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.impl.TGUndoableEditComposite;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackGeneric;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Undoes a recording as one step, however many tracks and takes it made, by restoring each armed track as it was. A
 * recording that made no take is no undo step.
 */
public class TGUndoableRecordingController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGUndoableEditComposite edit = new TGUndoableEditComposite();
		for (TGTrack track : armedTracks(context, actionContext)) {
			edit.addEdit(TGUndoableTrackGeneric.startUndo(context, track));
		}
		return edit;
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		if (!Boolean.TRUE.equals(actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS))) {
			return null;
		}
		List<TGUndoableEdit> edits = ((TGUndoableEditComposite) undoableEdit).getEdits();
		List<TGTrack> tracks = armedTracks(context, actionContext);
		for (int i = 0; i < edits.size(); i++) {
			((TGUndoableTrackGeneric) edits.get(i)).endUndo(tracks.get(i));
		}
		return undoableEdit;
	}

	private static List<TGTrack> armedTracks(TGContext context, TGActionContext actionContext) {
		TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		return TGAudioSession.getInstance(context).getArmedTracks(song);
	}
}
