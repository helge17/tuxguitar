package app.tuxguitar.editor.undo.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUndoableCloneTrack extends TGUndoableTrackBase{

	private int doAction;
	private int trackNumber;

	private TGUndoableCloneTrack(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.cloneTrack(actionContext, getSong(), getSongManager().getTrack(getSong(), this.trackNumber));
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.removeTrack(actionContext, getSong(), getSongManager().getLastTrack(getSong()));
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
