package org.herac.tuxguitar.app.undo.undoables.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTempo;

public class UndoableChangeTempo implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private List<TGTempo> undoableTempos;
	private List<TGTempo> redoableTempos;
	
	private UndoableChangeTempo(){
		super();
		this.undoableTempos = new ArrayList<TGTempo>();
		this.redoableTempos = new ArrayList<TGTempo>();
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
	
	private void getTempos(List<TGTempo> list){
		Iterator<TGMeasureHeader> it = getSong().getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			list.add(header.getTempo().clone(TuxGuitar.getInstance().getSongManager().getFactory()));
		}
	}
	
	private void setTempos(List<TGTempo> tempos){
		int length = tempos.size();
		if(length != getSong().countMeasureHeaders()){
			return;
		}
		for(int i =0; i < length; i ++){
			TGTempo tempo = ((TGTempo)tempos.get(i)).clone(TuxGuitar.getInstance().getSongManager().getFactory());
			TuxGuitar.getInstance().getSongManager().changeTempo(TuxGuitar.getInstance().getSongManager().getMeasureHeader(getSong(),i + 1),tempo);
		}
		TuxGuitar.getInstance().updateSong();
	}
	
	public static TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
