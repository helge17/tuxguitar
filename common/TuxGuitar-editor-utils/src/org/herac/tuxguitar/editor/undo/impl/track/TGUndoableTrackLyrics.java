package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTrackLyrics extends TGUndoableTrackBase {
	
	private int doAction;
	private int trackNumber;
	private TGLyric undoLyric;
	private TGLyric redoLyric;
	
	private TGUndoableTrackLyrics(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.setTrackLyrics(actionContext, this.getTrack(this.trackNumber), this.redoLyric.clone(getSongManager().getFactory()));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.setTrackLyrics(actionContext, this.getTrack(this.trackNumber), this.undoLyric.clone(getSongManager().getFactory()));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableTrackLyrics startUndo(TGContext context, TGTrack track){
		TGUndoableTrackLyrics undoable = new TGUndoableTrackLyrics(context);
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = track.getNumber();
		undoable.undoLyric = track.getLyrics().clone(new TGFactory());
		
		return undoable;
	}
	
	public TGUndoableTrackLyrics endUndo(TGTrack track){
		this.redoLyric = track.getLyrics().clone(new TGFactory());
		
		return this;
	}
	
	public TGTrack getTrack(int number) {
		return getSongManager().getTrack(getSong(), number);
	}
	
	public TGChannel getChannel(int channelId) {
		return getSongManager().getChannel(getSong(), channelId);
	}
}
