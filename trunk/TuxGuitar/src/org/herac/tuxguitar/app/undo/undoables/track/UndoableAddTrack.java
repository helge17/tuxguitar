package org.herac.tuxguitar.app.undo.undoables.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableAddTrack implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGTrack redoableTrack;
	
	private UndoableAddTrack(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TuxGuitar.getInstance().getSongManager().addTrack(getSong(), cloneTrack(this.redoableTrack));
		TuxGuitar.getInstance().updateSong();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TuxGuitar.getInstance().getSongManager().removeTrack(getSong(), cloneTrack(this.redoableTrack));
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
	
	public static UndoableAddTrack startUndo(){
		UndoableAddTrack undoable = new UndoableAddTrack();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		
		return undoable;
	}
	
	public UndoableAddTrack endUndo(TGTrack track){
		this.redoCaret = new UndoableCaretHelper();
		this.redoableTrack = cloneTrack(track);
		return this;
	}
	
	private static TGTrack cloneTrack(TGTrack track){
		return track.clone(TuxGuitar.getInstance().getSongManager().getFactory(), getSong());
	}
	
	public static TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
