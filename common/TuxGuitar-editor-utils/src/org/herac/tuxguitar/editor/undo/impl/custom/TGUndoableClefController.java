package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableClefController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGTrack track = ((TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		
		return TGUndoableClef.startUndo(context, track, measure);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		int clef = ((Integer) actionContext.getAttribute(TGChangeClefAction.ATTRIBUTE_CLEF)).intValue();
		boolean applyToEnd = ((Boolean) actionContext.getAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END)).booleanValue();
		
		return ((TGUndoableClef) undoableEdit).endUndo(clef, applyToEnd);
	}
}
