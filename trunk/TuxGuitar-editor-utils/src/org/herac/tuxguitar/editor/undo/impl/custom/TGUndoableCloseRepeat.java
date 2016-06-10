package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

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
