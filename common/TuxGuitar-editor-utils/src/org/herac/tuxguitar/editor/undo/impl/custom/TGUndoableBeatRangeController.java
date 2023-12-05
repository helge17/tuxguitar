package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditComposite;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

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
