package org.herac.tuxguitar.app.undo.undoables.measure;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.song.helpers.TGSongSegmentHelper;
import org.herac.tuxguitar.song.models.TGSong;

public class UndoableInsertMeasure implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGSongSegment tracksMeasures;
	private long insertPosition;
	private int toTrack;
	private int copyCount;
	private int fromNumber;
	private long theMove;
	
	public UndoableInsertMeasure(int toTrack){
		Caret caret = getCaret();
		this.doAction = UNDO_ACTION;
		this.toTrack = toTrack;
		this.undoCaret = new UndoableCaretHelper();
		this.insertPosition = caret.getMeasure().getStart();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongSegmentHelper helper = new TGSongSegmentHelper(TuxGuitar.getInstance().getSongManager());
		helper.insertMeasures(getSong(), this.tracksMeasures.clone(TuxGuitar.getInstance().getSongManager().getFactory()),this.fromNumber,this.theMove,this.toTrack);
		TuxGuitar.getInstance().updateSong();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		for(int i = 0;i < this.copyCount;i ++){
			TuxGuitar.getInstance().getSongManager().removeMeasure(getSong(), this.insertPosition);
		}
		TuxGuitar.getInstance().updateSong();
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public UndoableInsertMeasure endUndo(TGSongSegment tracksMeasures,int copyCount,int fromNumber,long theMove){
		this.redoCaret = new UndoableCaretHelper();
		this.copyCount = copyCount;
		this.tracksMeasures = tracksMeasures;
		this.fromNumber = fromNumber;
		this.theMove = theMove;
		return this;
	}
	
	private static Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	public TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
