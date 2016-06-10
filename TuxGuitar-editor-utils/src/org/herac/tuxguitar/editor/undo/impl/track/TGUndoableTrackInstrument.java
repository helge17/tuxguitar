package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTrackInstrument extends TGUndoableTrackBase {
	
	private int doAction;
	private int trackNumber;
	private int undoChannelId;
	private int redoChannelId;
	
	private TGUndoableTrackInstrument(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.setTrackChannel(actionContext, this.getTrack(this.trackNumber), this.getChannel(this.redoChannelId));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.setTrackChannel(actionContext, this.getTrack(this.trackNumber), this.getChannel(this.undoChannelId));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableTrackInstrument startUndo(TGContext context, TGTrack track){
		TGUndoableTrackInstrument undoable = new TGUndoableTrackInstrument(context);
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = track.getNumber();
		undoable.undoChannelId = track.getChannelId();
		
		return undoable;
	}
	
	public TGUndoableTrackInstrument endUndo(TGTrack track){
		this.redoChannelId = track.getChannelId();

		return this;
	}
	
	public TGTrack getTrack(int number) {
		return getSongManager().getTrack(getSong(), number);
	}
	
	public TGChannel getChannel(int channelId) {
		return getSongManager().getChannel(getSong(), channelId);
	}
}
