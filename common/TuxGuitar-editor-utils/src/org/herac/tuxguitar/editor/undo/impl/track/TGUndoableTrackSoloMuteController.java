package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTrackSoloMuteController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableTrackSoloMute.startUndo(context, (TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		return ((TGUndoableTrackSoloMute) undoableEdit).endUndo((TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
	}
}
