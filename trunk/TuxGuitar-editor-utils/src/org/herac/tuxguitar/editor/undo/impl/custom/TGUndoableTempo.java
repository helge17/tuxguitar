package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTempo extends TGUndoableEditBase{
	
	private int doAction;
	private long position;
	private TGTempo undoableTempo;
	private TGTempo redoableTempo;
	
	private TGUndoableTempo(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeTempo(actionContext, this.getMeasureHeaderAt(this.position), this.redoableTempo);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeTempo(actionContext, this.getMeasureHeaderAt(this.position), this.undoableTempo);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableTempo startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableTempo undoable = new TGUndoableTempo(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();
		undoable.undoableTempo = undoable.createTempoClone(header);
		return undoable;
	}
	
	public TGUndoableTempo endUndo(TGMeasureHeader header) {
		this.redoableTempo = createTempoClone(header);
		return this;
	}
	
	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}
	
	public TGTempo createTempoClone(TGMeasureHeader header) {
		return header.getTempo().clone(getSongManager().getFactory());
	}
	
	public void changeTempo(TGActionContext context, TGMeasureHeader header, TGTempo tempo) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeTempoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, tempo);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
