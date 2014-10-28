package org.herac.tuxguitar.app.undo.undoables.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableTrackGeneric implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private UndoTrack undoTrack;
	private RedoTrack redoTrack;
	
	private UndoableTrackGeneric(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		this.redoTrack.redo();
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		this.undoTrack.undo();
		this.undoCaret.update();
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	
	public static UndoableTrackGeneric startUndo(TGTrack track){
		UndoableTrackGeneric undoable = new UndoableTrackGeneric();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoTrack = undoable.new UndoTrack(track);
		return undoable;
	}
	
	public UndoableTrackGeneric endUndo(TGTrack track){
		this.redoCaret = new UndoableCaretHelper();
		this.redoTrack = new RedoTrack(track);
		return this;
	}
	
	public TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
	
	private class UndoTrack{
		private TGTrack track;
		
		public UndoTrack(TGTrack track){
			if(track != null){
				this.track = track.clone(TuxGuitar.getInstance().getSongManager().getFactory(), getSong());
			}
		}
		
		public void undo(){
			if(this.track != null){
				while( getSong().countMeasureHeaders() < this.track.countMeasures() ){
					TuxGuitar.getInstance().getSongManager().addNewMeasureBeforeEnd(getSong());
				}
				while( getSong().countMeasureHeaders() > this.track.countMeasures() ){
					TuxGuitar.getInstance().getSongManager().removeLastMeasureHeader(getSong());
				}
				TuxGuitar.getInstance().getSongManager().replaceTrack(getSong(), this.track);
				TuxGuitar.getInstance().updateSong();
			}
		}
	}
	
	private class RedoTrack{
		private TGTrack track;
		
		public RedoTrack(TGTrack track){
			if(track != null){
				this.track = track.clone(TuxGuitar.getInstance().getSongManager().getFactory(), getSong());
			}
		}
		
		public void redo(){
			if(this.track != null){
				while( getSong().countMeasureHeaders() < this.track.countMeasures() ){
					TuxGuitar.getInstance().getSongManager().addNewMeasureBeforeEnd(getSong());
				}
				while( getSong().countMeasureHeaders() > this.track.countMeasures() ){
					TuxGuitar.getInstance().getSongManager().removeLastMeasureHeader(getSong());
				}
				TuxGuitar.getInstance().getSongManager().replaceTrack(getSong(), this.track);
				TuxGuitar.getInstance().updateSong();
			}
		}
	}
}
