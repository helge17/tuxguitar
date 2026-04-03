package app.tuxguitar.app.undo.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUndoableMarkerGenericController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGMarker currentMarker = null;
		TGMarker marker = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		if( marker != null ) {
			TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGSongManager songManager = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
			currentMarker = songManager.getMarker(song, marker.getMeasure());
		}

		return TGUndoableMarkerGeneric.startUndo(context, currentMarker);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		TGMarker currentMarker = null;
		TGMarker marker = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		if( marker != null ) {
			TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGSongManager songManager = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
			currentMarker = songManager.getMarker(song, marker.getMeasure());
		}

		return ((TGUndoableMarkerGeneric) undoableEdit).endUndo(currentMarker);
	}
}
