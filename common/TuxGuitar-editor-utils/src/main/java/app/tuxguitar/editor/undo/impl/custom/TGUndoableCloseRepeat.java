package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUndoableCloseRepeat extends TGUndoableEditBase {

	private int doAction;
	private long position;
	private int undoRepeatClose;
	private int redoRepeatClose;

	private TGUndoableCloseRepeat(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeCloseRepeat(actionContext, getMeasureHeaderAt(this.position), this.redoRepeatClose);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeCloseRepeat(actionContext, getMeasureHeaderAt(this.position), this.undoRepeatClose);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableCloseRepeat startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableCloseRepeat undoable = new TGUndoableCloseRepeat(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();
		undoable.undoRepeatClose = header.getRepeatClose();

		return undoable;
	}

	public TGUndoableCloseRepeat endUndo(int redoRepeatClose){
		this.redoRepeatClose = redoRepeatClose;
		return this;
	}

	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}

	public void changeCloseRepeat(TGActionContext context, TGMeasureHeader header, Integer repeatCount) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRepeatCloseAction.NAME);
		tgActionProcessor.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, repeatCount);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
