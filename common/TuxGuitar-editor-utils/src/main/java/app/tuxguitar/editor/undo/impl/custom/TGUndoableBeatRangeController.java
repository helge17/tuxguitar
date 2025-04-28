package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.impl.TGUndoableEditComposite;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGeneric;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGUndoableBeatRangeController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGBeatRange beats = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGUndoableEditComposite editComposite = new TGUndoableEditComposite();
		for (TGMeasure editedMeasure : beats.getMeasures()) {
			editComposite.addEdit(TGUndoableMeasureGeneric.startUndo(context, editedMeasure));
		}
		return editComposite;
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		TGBeatRange beats = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGUndoableEditComposite undoableEditComposite = (TGUndoableEditComposite) undoableEdit;
		int i = 0;
		for (TGMeasure editedMeasure : beats.getMeasures()) {
			TGUndoableMeasureGeneric measureEdit = (TGUndoableMeasureGeneric) undoableEditComposite.getEdits().get(i);
			measureEdit.endUndo(editedMeasure);
			i++;
		}
		return undoableEdit;
	}
}
