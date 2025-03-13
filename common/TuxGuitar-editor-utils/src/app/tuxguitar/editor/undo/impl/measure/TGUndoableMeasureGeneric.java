package app.tuxguitar.editor.undo.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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
