package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableMoveTrackDown extends TGUndoableTrackBase {
	
	private int doAction;
	private int trackNumber;
	
	private TGUndoableMoveTrackDown(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.moveTrackDown(actionContext, getSong(), getSongManager().getTrack(getSong(), this.trackNumber - 1));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.moveTrackUp(actionContext, getSong(), getSongManager().getTrack(getSong(), this.trackNumber));		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableMoveTrackDown startUndo(TGContext context){
		TGUndoableMoveTrackDown undoable = new TGUndoableMoveTrackDown(context);
		undoable.doAction = UNDO_ACTION;
		
		return undoable;
	}
	
	public TGUndoableMoveTrackDown endUndo(TGTrack track){
		this.trackNumber = track.getNumber();
		
		return this;
	}
}
