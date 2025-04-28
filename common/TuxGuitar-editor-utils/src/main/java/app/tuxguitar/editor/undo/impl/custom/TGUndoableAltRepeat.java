package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUndoableAltRepeat extends TGUndoableEditBase {
	private int doAction;
	private long position;
	private int undoRepeatAlternative;
	private int redoRepeatAlternative;

	private TGUndoableAltRepeat(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeAlternativeRepeat(actionContext, getMeasureHeaderAt(this.position), this.redoRepeatAlternative);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeAlternativeRepeat(actionContext, getMeasureHeaderAt(this.position), this.undoRepeatAlternative);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableAltRepeat startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableAltRepeat undoable = new TGUndoableAltRepeat(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();
		undoable.undoRepeatAlternative = header.getRepeatAlternative();

		return undoable;
	}

	public TGUndoableAltRepeat endUndo(int redoRepeatAlternative){
		this.redoRepeatAlternative = redoRepeatAlternative;
		return this;
	}

	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}

	public void changeAlternativeRepeat(TGActionContext context, TGMeasureHeader header, Integer repeatAlternative) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRepeatAlternativeAction.NAME);
		tgActionProcessor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
