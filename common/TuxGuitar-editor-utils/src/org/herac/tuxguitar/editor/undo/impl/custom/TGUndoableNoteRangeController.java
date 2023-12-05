package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditComposite;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

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
