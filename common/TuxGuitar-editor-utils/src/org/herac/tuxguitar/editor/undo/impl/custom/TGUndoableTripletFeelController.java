package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTripletFeelController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableTripletFeel.startUndo(context, (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		int tripletFeel = ((Integer) actionContext.getAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL)).intValue();
		boolean applyToEnd = ((Boolean) actionContext.getAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END)).booleanValue();
		
		return ((TGUndoableTripletFeel) undoableEdit).endUndo(tripletFeel, applyToEnd);
	}
}
