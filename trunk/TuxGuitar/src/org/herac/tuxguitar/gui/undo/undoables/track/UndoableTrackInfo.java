package org.herac.tuxguitar.gui.undo.undoables.track;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableTrackInfo implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private String undoName;
	private String redoName;
	private TGColor undoColor;
	private TGColor redoColor;
	private int undoOffset;
	private int redoOffset;
	
	private UndoableTrackInfo(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.getTrackManager().changeInfo(manager.getTrack(this.trackNumber),this.redoName,this.redoColor.clone(manager.getFactory()),this.redoOffset);
		TuxGuitar.instance().fireUpdate();
		TuxGuitar.instance().getMixer().update();
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.getTrackManager().changeInfo(manager.getTrack(this.trackNumber),this.undoName,this.undoColor.clone(manager.getFactory()),this.undoOffset);
		TuxGuitar.instance().fireUpdate();
		TuxGuitar.instance().getMixer().update();
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableTrackInfo startUndo(TGTrack track){
		UndoableTrackInfo undoable = new UndoableTrackInfo();
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = track.getNumber();
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoName = track.getName();
		undoable.undoColor = track.getColor().clone(TuxGuitar.instance().getSongManager().getFactory());
		undoable.undoOffset = track.getOffset();
		
		return undoable;
	}
	
	public UndoableTrackInfo endUndo(TGTrack track){
		this.redoCaret = new UndoableCaretHelper();
		this.redoName = track.getName();
		this.redoColor = track.getColor().clone(TuxGuitar.instance().getSongManager().getFactory());
		this.redoOffset = track.getOffset();
		
		return this;
	}
	
}
