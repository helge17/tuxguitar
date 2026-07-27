package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGDoubleBarAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUndoableDoubleBar extends TGUndoableEditBase{

	private int doAction;
	private long position;

	private TGUndoableDoubleBar(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeDoubleBar(actionContext, getMeasureHeaderAt(this.position));
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeDoubleBar(actionContext, getMeasureHeaderAt(this.position));
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableDoubleBar startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableDoubleBar undoable = new TGUndoableDoubleBar(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();

		return undoable;
	}

	public TGUndoableDoubleBar endUndo(){
		return this;
	}

	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}

	public void changeDoubleBar(TGActionContext context, TGMeasureHeader header) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGDoubleBarAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
