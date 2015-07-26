package org.herac.tuxguitar.editor.undo.impl.measure;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.song.helpers.TGSongSegmentHelper;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
	
	public void redo() throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.removeMeasure(getSong(), this.number);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.insertMeasures(getSong(), this.tracksMeasures.clone(getSongManager().getFactory()), this.number, 0, 0);
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
	
	public void removeMeasure(TGSong song, Integer number) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRemoveMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGRemoveMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		this.processByPassUndoableAction(tgActionProcessor);
	}
	
	public void insertMeasures(TGSong song, TGSongSegment segment, int fromNumber, int toTrack, long theMove) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGInsertMeasuresAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_SONG_SEGMENT, segment);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_FROM_NUMBER, fromNumber);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_TO_TRACK, toTrack);
		tgActionProcessor.setAttribute(TGInsertMeasuresAction.ATTRIBUTE_THE_MOVE, theMove);
		this.processByPassUndoableAction(tgActionProcessor);
	}
}
