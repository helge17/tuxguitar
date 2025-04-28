package app.tuxguitar.editor.undo.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUndoableTrackInfo extends TGUndoableTrackBase {

	private int doAction;
	private int trackNumber;
	private String undoName;
	private String redoName;
	private TGColor undoColor;
	private TGColor redoColor;
	private int undoOffset;
	private int redoOffset;

	private TGUndoableTrackInfo(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.setTrackInfo(actionContext, this.getTrack(this.trackNumber), this.redoName, this.redoOffset, this.redoColor);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.setTrackInfo(actionContext, this.getTrack(this.trackNumber), this.undoName, this.undoOffset, this.undoColor);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableTrackInfo startUndo(TGContext context, TGTrack track){
		TGUndoableTrackInfo undoable = new TGUndoableTrackInfo(context);
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = track.getNumber();
		undoable.undoName = track.getName();
		undoable.undoOffset = track.getOffset();
		undoable.undoColor = track.getColor().clone(new TGFactory());

		return undoable;
	}

	public TGUndoableTrackInfo endUndo(TGTrack track){
		this.redoName = track.getName();
		this.redoOffset = track.getOffset();
		this.redoColor = track.getColor().clone(new TGFactory());

		return this;
	}

	public TGTrack getTrack(int number) {
		return this.getSongManager().getTrack(getSong(), number);
	}
}
