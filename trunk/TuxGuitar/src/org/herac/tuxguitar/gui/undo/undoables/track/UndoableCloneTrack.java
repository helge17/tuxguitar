package org.herac.tuxguitar.gui.undo.undoables.track;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;

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
		TuxGuitar.instance().getMixer().update();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TuxGuitar.instance().getSongManager().removeTrack(TuxGuitar.instance().getSongManager().getLastTrack());
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
