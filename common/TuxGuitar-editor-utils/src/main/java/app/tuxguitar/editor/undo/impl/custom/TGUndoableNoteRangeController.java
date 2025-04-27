package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.impl.TGUndoableEditComposite;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGeneric;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

/**
 * Created by tubus on 25.01.17.
 */
public class TGUndoableNoteRangeController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGNoteRange noteRange = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGUndoableEditComposite editComposite = new TGUndoableEditComposite();
		for (TGMeasure editedMeasure : noteRange.getMeasures()) {
			editComposite.addEdit(TGUndoableMeasureGeneric.startUndo(context, editedMeasure));
		}
		return editComposite;
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		TGNoteRange noteRange = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGUndoableEditComposite undoableEditComposite = (TGUndoableEditComposite) undoableEdit;
		int i = 0;
		for (TGMeasure editedMeasure : noteRange.getMeasures()) {
			TGUndoableMeasureGeneric measureEdit = (TGUndoableMeasureGeneric) undoableEditComposite.getEdits().get(i);
			measureEdit.endUndo(editedMeasure);
			i++;
		}
		return undoableEdit;
	}
}
