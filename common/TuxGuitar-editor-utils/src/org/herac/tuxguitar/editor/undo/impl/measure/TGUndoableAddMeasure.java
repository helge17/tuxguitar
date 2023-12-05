package org.herac.tuxguitar.editor.undo.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableAddMeasure extends TGUndoableEditBase{
	
	private int doAction;
	private int number;
	
	private TGUndoableAddMeasure(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.addMeasure(actionContext, getSong(), this.number);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.removeMeasure(actionContext, getSong(), this.number);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableAddMeasure startUndo(TGContext context, int number){
		TGUndoableAddMeasure undoable = new TGUndoableAddMeasure(context);
		undoable.doAction = UNDO_ACTION;
		undoable.number = number;
		return undoable;
	}
	
	public TGUndoableAddMeasure endUndo(){
		return this;
	}
	
	public void addMeasure(TGActionContext context, TGSong song, Integer number) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGAddMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void removeMeasure(TGActionContext context, TGSong song, Integer number) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRemoveMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGRemoveMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
