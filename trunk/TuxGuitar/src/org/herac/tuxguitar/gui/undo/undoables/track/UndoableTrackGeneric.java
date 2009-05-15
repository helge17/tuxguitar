package org.herac.tuxguitar.gui.undo.undoables.track;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
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
	
	private class UndoTrack{
		private TGTrack track;
		
		public UndoTrack(TGTrack track){
			if(track != null){
				this.track = track.clone(TuxGuitar.instance().getSongManager().getFactory(),TuxGuitar.instance().getSongManager().getSong());
			}
		}
		
		public void undo(){
			if(this.track != null){
				while( TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders() < this.track.countMeasures() ){
					TuxGuitar.instance().getSongManager().addNewMeasureBeforeEnd();
				}
				while( TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders() > this.track.countMeasures() ){
					TuxGuitar.instance().getSongManager().removeLastMeasureHeader();
				}
				TuxGuitar.instance().getSongManager().replaceTrack(this.track);
				TuxGuitar.instance().fireUpdate();
				TuxGuitar.instance().getMixer().update();
			}
		}
	}
	
	private class RedoTrack{
		private TGTrack track;
		
		public RedoTrack(TGTrack track){
			if(track != null){
				this.track = track.clone(TuxGuitar.instance().getSongManager().getFactory(),TuxGuitar.instance().getSongManager().getSong());
			}
		}
		
		public void redo(){
			if(this.track != null){
				while( TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders() < this.track.countMeasures() ){
					TuxGuitar.instance().getSongManager().addNewMeasureBeforeEnd();
				}
				while( TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders() > this.track.countMeasures() ){
					TuxGuitar.instance().getSongManager().removeLastMeasureHeader();
				}
				TuxGuitar.instance().getSongManager().replaceTrack(this.track);
				TuxGuitar.instance().fireUpdate();
				TuxGuitar.instance().getMixer().update();
			}
		}
	}
}
