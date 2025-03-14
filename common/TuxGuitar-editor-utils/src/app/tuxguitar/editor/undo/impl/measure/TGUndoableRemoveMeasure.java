package app.tuxguitar.editor.undo.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.song.helpers.TGSongSegmentHelper;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUndoableRemoveMeasure extends TGUndoableEditBase{

	private int doAction;
	private TGSongSegment tracksMeasures;
	private int number;

	public TGUndoableRemoveMeasure(TGContext context, int number){
		super(context);

		this.doAction = UNDO_ACTION;
		this.number = number;
		this.tracksMeasures = new TGSongSegmentHelper(getSongManager()).copyMeasures(getSong(), number, number);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.removeMeasure(actionContext, getSong(), this.number);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.insertMeasures(actionContext, getSong(), this.tracksMeasures.clone(getSongManager().getFactory()), this.number, 0, 0);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public TGUndoableRemoveMeasure endUndo(){
		return this;
	}

	public void removeMeasure(TGActionContext context, TGSong song, Integer number) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRemoveMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGRemoveMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}

	public void insertMeasures(TGActionContext context, TGSong song, TGSongSegment segment, int fromNumber, int toTrack, long theMove) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGInsertMeasuresAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_SONG_SEGMENT, segment);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_FROM_NUMBER, fromNumber);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_TO_TRACK, toTrack);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_THE_MOVE, theMove);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
