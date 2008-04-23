package org.herac.tuxguitar.gui.undo.undoables.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTempo;

public class UndoableChangeTempo implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private List undoableTempos;
	private List redoableTempos;
	
	private UndoableChangeTempo(){
		super();
		this.undoableTempos = new ArrayList();
		this.redoableTempos = new ArrayList();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		this.setTempos(this.redoableTempos);
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		this.setTempos(this.undoableTempos);
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableChangeTempo startUndo(){
		UndoableChangeTempo undoable = new UndoableChangeTempo();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.getTempos(undoable.undoableTempos);
		return undoable;
	}
	
	public UndoableChangeTempo endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		this.getTempos(this.redoableTempos);
		return this;
	}
	
	private void getTempos(List list){
		Iterator it = TuxGuitar.instance().getSongManager().getSong().getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			list.add(header.getTempo().clone(TuxGuitar.instance().getSongManager().getFactory()));
		}
	}
	
	private void setTempos(List tempos){
		int length = tempos.size();
		if(length != TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders()){
			return;
		}
		for(int i =0; i < length; i ++){
			TGTempo tempo = ((TGTempo)tempos.get(i)).clone(TuxGuitar.instance().getSongManager().getFactory());
			TuxGuitar.instance().getSongManager().changeTempo(TuxGuitar.instance().getSongManager().getMeasureHeader(i + 1),tempo);
		}
		TuxGuitar.instance().fireUpdate();
	}
}
