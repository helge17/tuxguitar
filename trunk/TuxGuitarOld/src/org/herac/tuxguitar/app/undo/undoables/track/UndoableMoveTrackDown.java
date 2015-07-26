package org.herac.tuxguitar.app.undo.undoables.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableMoveTrackDown implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	
	private UndoableMoveTrackDown(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongManager manager = TuxGuitar.getInstance().getSongManager();
		manager.moveTrackDown(getSong(), manager.getTrack(getSong(), this.trackNumber - 1));
		TuxGuitar.getInstance().updateSong();
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGSongManager manager = TuxGuitar.getInstance().getSongManager();
		manager.moveTrackUp(getSong(), manager.getTrack(getSong(), this.trackNumber));
		TuxGuitar.getInstance().updateSong();
		this.undoCaret.update();
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	
	public static UndoableMoveTrackDown startUndo(){
		UndoableMoveTrackDown undoable = new UndoableMoveTrackDown();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		
		return undoable;
	}
	
	public UndoableMoveTrackDown endUndo(TGTrack track){
		this.redoCaret = new UndoableCaretHelper();
		this.trackNumber = track.getNumber();
		
		return this;
	}
	
	public TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
