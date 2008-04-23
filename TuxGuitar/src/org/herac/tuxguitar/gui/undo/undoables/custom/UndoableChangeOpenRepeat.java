package org.herac.tuxguitar.gui.undo.undoables.custom;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;

public class UndoableChangeOpenRepeat implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private long position;
	
	private UndoableChangeOpenRepeat(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.changeOpenRepeat(this.position);
		TGMeasure measure = manager.getTrackManager().getMeasureAt(manager.getFirstTrack(),this.position);
		TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().fireUpdate(measure.getNumber());
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.changeOpenRepeat(this.position);
		TGMeasure measure = manager.getTrackManager().getMeasureAt(manager.getFirstTrack(),this.position);
		TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().fireUpdate(measure.getNumber());
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableChangeOpenRepeat startUndo(){
		UndoableChangeOpenRepeat undoable = new UndoableChangeOpenRepeat();
		Caret caret = getCaret();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.position = caret.getPosition();
		
		return undoable;
	}
	
	public UndoableChangeOpenRepeat endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		return this;
	}
	
	private static Caret getCaret(){
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
	}
	
}
