package org.herac.tuxguitar.gui.undo.undoables.track;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableTrackInstrument implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private short undoInstrument;
	private short redoInstrument;
	private boolean undoPercussion;
	private boolean redoPercussion;
	
	private UndoableTrackInstrument(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.getTrackManager().changeInstrument(manager.getTrack(this.trackNumber),this.redoInstrument,this.redoPercussion);
		TuxGuitar.instance().fireUpdate();
		TuxGuitar.instance().getMixer().updateValues();
		if (TuxGuitar.instance().getPlayer().isRunning()) {
			TuxGuitar.instance().getPlayer().updatePrograms();
		}
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.getTrackManager().changeInstrument(manager.getTrack(this.trackNumber),this.undoInstrument,this.undoPercussion);
		TuxGuitar.instance().fireUpdate();
		TuxGuitar.instance().getMixer().updateValues();
		if (TuxGuitar.instance().getPlayer().isRunning()) {
			TuxGuitar.instance().getPlayer().updatePrograms();
		}
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
		undoable.undoInstrument = track.getChannel().getInstrument();
		undoable.undoPercussion = track.isPercussionTrack();
		
		return undoable;
	}
	
	public UndoableTrackInstrument endUndo(TGTrack track){
		this.redoCaret = new UndoableCaretHelper();
		this.redoInstrument = track.getChannel().getInstrument();
		this.redoPercussion = track.isPercussionTrack();
		
		return this;
	}
	
}
