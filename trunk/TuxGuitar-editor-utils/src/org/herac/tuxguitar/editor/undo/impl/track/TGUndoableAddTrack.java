package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableAddTrack extends TGUndoableTrackBase{
	
	private int doAction;
	private TGTrack redoableTrack;
	
	private TGUndoableAddTrack(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.addTrack(actionContext, getSong(), this.redoableTrack.clone(getSongManager().getFactory(), getSong()));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.removeTrack(actionContext, getSong(), this.redoableTrack.clone(getSongManager().getFactory(), getSong()));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableAddTrack startUndo(TGContext context){
		TGUndoableAddTrack undoable = new TGUndoableAddTrack(context);
		undoable.doAction = UNDO_ACTION;
		
		return undoable;
	}
	
	public TGUndoableAddTrack endUndo(TGTrack track){
		this.redoableTrack = track.clone(new TGFactory(), getSong());
		
		return this;
	}
}
