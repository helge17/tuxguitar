package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

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
