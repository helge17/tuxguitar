package org.herac.tuxguitar.app.undo.undoables.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableTrackInstrument implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private int undoChannelId;
	private int redoChannelId;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	
	private UndoableTrackInstrument(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongManager tgSongManager = TuxGuitar.getInstance().getSongManager();
		tgSongManager.getTrackManager().changeChannel(tgSongManager.getTrack(this.trackNumber),this.redoChannelId);
		
		TuxGuitar.getInstance().updateCache(true);
		
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGSongManager tgSongManager = TuxGuitar.getInstance().getSongManager();
		tgSongManager.getTrackManager().changeChannel(tgSongManager.getTrack(this.trackNumber),this.undoChannelId);
		
		TuxGuitar.getInstance().updateCache(true);
		
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableTrackInstrument startUndo(TGTrack track){
		UndoableTrackInstrument undoable = new UndoableTrackInstrument();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.trackNumber = track.getNumber();
		undoable.undoChannelId = track.getChannelId();
		
		return undoable;
	}
	
	public UndoableTrackInstrument endUndo(TGTrack track){
		this.redoCaret = new UndoableCaretHelper();
		this.redoChannelId = track.getChannelId();
		
		return this;
	}
	
}
