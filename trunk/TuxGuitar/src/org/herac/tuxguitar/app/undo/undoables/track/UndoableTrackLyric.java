package org.herac.tuxguitar.app.undo.undoables.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableTrackLyric implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGLyric undoLyric;
	private TGLyric redoLyric;
	private int undoCaretPosition;
	private int redoCaretPosition;
	
	private UndoableTrackLyric(){
		this.undoLyric = TuxGuitar.getInstance().getSongManager().getFactory().newLyric();
		this.redoLyric = TuxGuitar.getInstance().getSongManager().getFactory().newLyric();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGTrack track = TuxGuitar.getInstance().getSongManager().getTrack(getSong(), this.trackNumber);
		track.getLyrics().copyFrom(this.redoLyric);
		TuxGuitar.getInstance().getLyricEditor().setCaretPosition(this.redoCaretPosition);
		TuxGuitar.getInstance().getLyricEditor().update();
		TuxGuitar.getInstance().updateCache(false);
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGTrack track = TuxGuitar.getInstance().getSongManager().getTrack(getSong(), this.trackNumber);
		track.getLyrics().copyFrom(this.undoLyric);
		TuxGuitar.getInstance().getLyricEditor().setCaretPosition(this.undoCaretPosition);
		TuxGuitar.getInstance().getLyricEditor().update();
		TuxGuitar.getInstance().updateCache(false);
		this.undoCaret.update();
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableTrackLyric startUndo(TGTrack track,int undoCaretPosition){
		UndoableTrackLyric undoable = new UndoableTrackLyric();
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = track.getNumber();
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoCaretPosition = undoCaretPosition;
		undoable.undoLyric.copyFrom( track.getLyrics() );
		return undoable;
	}
	
	public UndoableTrackLyric endUndo(TGTrack track,int redoCaretPosition){
		this.redoCaret = new UndoableCaretHelper();
		this.redoCaretPosition = redoCaretPosition;
		this.redoLyric.copyFrom( track.getLyrics() );
		return this;
	}
	
	public TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
