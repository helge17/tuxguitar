package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableMoveTrackUp extends TGUndoableTrackBase {
	
	private int doAction;
	private int trackNumber;
	
	private TGUndoableMoveTrackUp(TGContext context){
		super(context);
	}
	
	public void redo() throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.moveTrackUp(getSong(), getSongManager().getTrack(getSong(), this.trackNumber + 1));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.moveTrackDown(getSong(), getSongManager().getTrack(getSong(), this.trackNumber));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableMoveTrackUp startUndo(TGContext context){
		TGUndoableMoveTrackUp undoable = new TGUndoableMoveTrackUp(context);
		undoable.doAction = UNDO_ACTION;
		
		return undoable;
	}
	
	public TGUndoableMoveTrackUp endUndo(TGTrack track){
		this.trackNumber = track.getNumber();
		
		return this;
	}
}
