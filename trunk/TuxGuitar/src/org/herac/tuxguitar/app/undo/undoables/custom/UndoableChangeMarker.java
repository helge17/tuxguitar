package org.herac.tuxguitar.app.undo.undoables.custom;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.marker.MarkerList;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGSong;

public class UndoableChangeMarker implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGMarker undoMarker;
	private TGMarker redoMarker;
	
	private UndoableChangeMarker(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		if(this.redoMarker != null){
			TuxGuitar.getInstance().getSongManager().updateMarker(getSong(),this.redoMarker.clone(TuxGuitar.getInstance().getSongManager().getFactory()));
			MarkerList.instance().update(true);
		}else if(this.undoMarker != null){
			TuxGuitar.getInstance().getSongManager().removeMarker(getSong(),this.undoMarker.clone(TuxGuitar.getInstance().getSongManager().getFactory()));
			MarkerList.instance().update(false);
		}
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		if(this.undoMarker != null){
			TuxGuitar.getInstance().getSongManager().updateMarker(getSong(),this.undoMarker.clone(TuxGuitar.getInstance().getSongManager().getFactory()));
			MarkerList.instance().update(true);
		}else if(this.redoMarker != null){
			TuxGuitar.getInstance().getSongManager().removeMarker(getSong(),this.redoMarker.clone(TuxGuitar.getInstance().getSongManager().getFactory()));
			MarkerList.instance().update(false);
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
	
	public static UndoableChangeMarker startUndo(TGMarker marker){
		UndoableChangeMarker undoable = new UndoableChangeMarker();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoMarker = (marker == null)?null:(TGMarker)marker.clone(TuxGuitar.getInstance().getSongManager().getFactory());
		
		return undoable;
	}
	
	public UndoableChangeMarker endUndo(TGMarker marker){
		this.redoCaret = new UndoableCaretHelper();
		this.redoMarker = (marker == null)?null:(TGMarker)marker.clone(TuxGuitar.getInstance().getSongManager().getFactory());
		return this;
	}
	
	public static TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
