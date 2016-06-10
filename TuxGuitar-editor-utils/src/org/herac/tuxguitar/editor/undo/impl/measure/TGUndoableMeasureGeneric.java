package org.herac.tuxguitar.editor.undo.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableMeasureGeneric extends TGUndoableEditBase {
	
	private int doAction;
	private int trackNumber;
	private TGMeasure undoMeasure;
	private TGMeasure redoMeasure;
	
	private TGUndoableMeasureGeneric(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.copyMeasureFrom(actionContext, this.getMeasure(this.redoMeasure.getNumber()), this.redoMeasure);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.copyMeasureFrom(actionContext, this.getMeasure(this.undoMeasure.getNumber()), this.undoMeasure);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableMeasureGeneric startUndo(TGContext context, TGMeasure measure ){
		TGFactory factory = new TGFactory();
		TGUndoableMeasureGeneric undoable = new TGUndoableMeasureGeneric(context);
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = measure.getTrack().getNumber();
		undoable.undoMeasure = measure.clone(factory, measure.getHeader().clone(factory));
		return undoable;
	}
	
	public TGUndoableMeasureGeneric endUndo( TGMeasure measure ){
		TGFactory factory = new TGFactory();
		this.redoMeasure = measure.clone(factory, measure.getHeader().clone(factory));
		return this;
	}
	
	public TGMeasure getMeasure(int number) {
		TGSongManager tgSongManager = this.getSongManager();
		TGTrack track = tgSongManager.getTrack(getSong(), this.trackNumber);
		return tgSongManager.getTrackManager().getMeasure(track, number);
	}
	
	public void copyMeasureFrom(TGActionContext context, TGMeasure measure, TGMeasure from) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCopyMeasureFromAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, measure.getHeader());
		tgActionProcessor.setAttribute(TGCopyMeasureFromAction.ATTRIBUTE_FROM, from);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
