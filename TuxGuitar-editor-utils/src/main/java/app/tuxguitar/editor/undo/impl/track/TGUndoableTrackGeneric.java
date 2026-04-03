package app.tuxguitar.editor.undo.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUndoableTrackGeneric extends TGUndoableTrackBase {

	private int doAction;
	private TGTrack undoTrack;
	private TGTrack redoTrack;

	private TGUndoableTrackGeneric(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.copyTrackFrom(actionContext, getSong(), this.getTrack(this.redoTrack.getNumber()), this.redoTrack);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.copyTrackFrom(actionContext, getSong(), this.getTrack(this.undoTrack.getNumber()), this.undoTrack);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableTrackGeneric startUndo(TGContext context, TGTrack track){
		TGUndoableTrackGeneric undoable = new TGUndoableTrackGeneric(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoTrack = track.clone(new TGFactory(), getSong(context));
		return undoable;
	}

	public TGUndoableTrackGeneric endUndo(TGTrack track){
		this.redoTrack = track.clone(new TGFactory(), getSong());
		return this;
	}

	public TGTrack getTrack(int number) {
		return this.getSongManager().getTrack(getSong(), number);
	}
}
