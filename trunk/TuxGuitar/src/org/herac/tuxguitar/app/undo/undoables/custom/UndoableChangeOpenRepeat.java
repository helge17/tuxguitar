package org.herac.tuxguitar.app.undo.undoables.custom;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;

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
		TGSongManager manager = TuxGuitar.getInstance().getSongManager();
		manager.changeOpenRepeat(getSong(),this.position);
		TGMeasure measure = manager.getTrackManager().getMeasureAt(manager.getFirstTrack(getSong()),this.position);
		TuxGuitar.getInstance().getTablatureEditor().getTablature().updateMeasure(measure.getNumber());
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGSongManager manager = TuxGuitar.getInstance().getSongManager();
		manager.changeOpenRepeat(getSong(),this.position);
		TGMeasure measure = manager.getTrackManager().getMeasureAt(manager.getFirstTrack(getSong()),this.position);
		TuxGuitar.getInstance().getTablatureEditor().getTablature().updateMeasure(measure.getNumber());
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
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	public static TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
