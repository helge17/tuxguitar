package org.herac.tuxguitar.app.undo.undoables.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;

public class UndoableCloneTrack implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	
	private UndoableCloneTrack(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TuxGuitar.instance().getSongManager().cloneTrack(TuxGuitar.instance().getSongManager().getTrack(this.trackNumber));
		TuxGuitar.instance().fireUpdate();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TuxGuitar.instance().getSongManager().removeTrack(TuxGuitar.instance().getSongManager().getLastTrack());
		TuxGuitar.instance().fireUpdate();
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableCloneTrack startUndo(){
		UndoableCloneTrack undoable = new UndoableCloneTrack();
		Caret caret = getCaret();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.trackNumber = caret.getTrack().getNumber();
		
		return undoable;
	}
	
	public UndoableCloneTrack endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		return this;
	}
	
	private static Caret getCaret(){
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
	}
	
}
