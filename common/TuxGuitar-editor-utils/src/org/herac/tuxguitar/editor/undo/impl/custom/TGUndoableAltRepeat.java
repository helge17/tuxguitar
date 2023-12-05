package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

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
