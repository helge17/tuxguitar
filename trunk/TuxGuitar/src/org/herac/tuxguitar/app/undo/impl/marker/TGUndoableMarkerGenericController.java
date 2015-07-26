package org.herac.tuxguitar.app.undo.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
