package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
