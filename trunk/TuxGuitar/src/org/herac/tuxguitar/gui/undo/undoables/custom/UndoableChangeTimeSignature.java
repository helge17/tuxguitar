package org.herac.tuxguitar.gui.undo.undoables.custom;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class UndoableChangeTimeSignature implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGSong song;
	private long tsStart;
	private boolean tsToEnd;
	private TGTimeSignature ts;
	
	private UndoableChangeTimeSignature(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TuxGuitar.instance().getTablatureEditor().getTablature().getSongManager().changeTimeSignature(this.tsStart,this.ts,this.tsToEnd);
		TuxGuitar.instance().fireUpdate();
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TGFactory factory = TuxGuitar.instance().getTablatureEditor().getTablature().getSongManager().getFactory();
		TGSong song = TuxGuitar.instance().getTablatureEditor().getTablature().getSongManager().getSong();
		this.song.copy(factory, song);
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
	
	public static UndoableChangeTimeSignature startUndo(){
		TGFactory factory = new TGFactory();
		TGSong song = TuxGuitar.instance().getTablatureEditor().getTablature().getSongManager().getSong();
		UndoableChangeTimeSignature undoable = new UndoableChangeTimeSignature();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.song = song.clone(factory);
		return undoable;
	}
	
	public UndoableChangeTimeSignature endUndo(TGTimeSignature timeSignature,long start, boolean toEnd){
		this.ts = timeSignature;
		this.tsStart = start;
		this.tsToEnd = toEnd;
		this.redoCaret = new UndoableCaretHelper();
		return this;
	}
}
