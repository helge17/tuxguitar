package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableOpenRepeat extends TGUndoableEditBase{
	
	private int doAction;
	private long position;
	
	private TGUndoableOpenRepeat(TGContext context){
		super(context);
	}
	
	public void redo() throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeOpenRepeat(getMeasureHeaderAt(this.position));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeOpenRepeat(getMeasureHeaderAt(this.position));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableOpenRepeat startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableOpenRepeat undoable = new TGUndoableOpenRepeat(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();
		
		return undoable;
	}
	
	public TGUndoableOpenRepeat endUndo(){
		return this;
	}
	
	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}
	
	public void changeOpenRepeat(TGMeasureHeader header) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRepeatOpenAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		this.processByPassUndoableAction(tgActionProcessor);
	}
}
