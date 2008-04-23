package org.herac.tuxguitar.gui.undo.undoables.custom;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;

public class UndoableChangeCloseRepeat implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private long position;
	private int undoRepeatClose;
	private int redoRepeatClose;
	
	private UndoableChangeCloseRepeat(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		manager.changeCloseRepeat(this.position,this.redoRepeatClose);
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
		manager.changeCloseRepeat(this.position,this.undoRepeatClose);
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
	
	public static UndoableChangeCloseRepeat startUndo(){
		Caret caret = getCaret();
		return startUndo(caret.getPosition(),caret.getMeasure().getRepeatClose());
	}
	
	public static UndoableChangeCloseRepeat startUndo(long position,int repeatClose){
		UndoableChangeCloseRepeat undoable = new UndoableChangeCloseRepeat();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.position = position;
		undoable.undoRepeatClose = repeatClose;
		
		return undoable;
	}
	
	public UndoableChangeCloseRepeat endUndo(int redoRepeatClose){
		this.redoCaret = new UndoableCaretHelper();
		this.redoRepeatClose = redoRepeatClose;
		return this;
	}
	
	private static Caret getCaret(){
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
	}
}
