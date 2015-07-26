package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableCloneTrack extends TGUndoableTrackBase{
	
	private int doAction;
	private int trackNumber;
	
	private TGUndoableCloneTrack(TGContext context){
		super(context);
	}
	
	public void redo() throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.cloneTrack(getSong(), getSongManager().getTrack(getSong(), this.trackNumber));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.removeTrack(getSong(), getSongManager().getLastTrack(getSong()));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableCloneTrack startUndo(TGContext context, TGTrack track){
		TGUndoableCloneTrack undoable = new TGUndoableCloneTrack(context);
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = track.getNumber();
		
		return undoable;
	}
	
	public TGUndoableCloneTrack endUndo(){
		return this;
	}
}
