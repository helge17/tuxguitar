package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableRemoveTrack extends TGUndoableTrackBase{
	
	private int doAction;
	private TGTrack undoableTrack;
	
	private TGUndoableRemoveTrack(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.removeTrack(actionContext, getSong(), this.undoableTrack.clone(getSongManager().getFactory(), getSong()));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.addTrack(actionContext, getSong(), this.undoableTrack.clone(getSongManager().getFactory(), getSong()));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableRemoveTrack startUndo(TGContext context, TGTrack track){
		TGUndoableRemoveTrack undoable = new TGUndoableRemoveTrack(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoableTrack = track.clone(new TGFactory(), getSong(context));
		
		return undoable;
	}
	
	public TGUndoableRemoveTrack endUndo(){
		return this;
	}
}
