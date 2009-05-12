package org.herac.tuxguitar.gui.undo.undoables.measure;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableMeasureGeneric implements UndoableEdit{
	private int doAction;
	private int trackNumber;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGMeasure undoMeasure;
	private TGMeasure redoMeasure;
	
	private UndoableMeasureGeneric(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		this.replace(this.redoMeasure);
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		this.replace(this.undoMeasure);
		this.undoCaret.update();
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	private void replace(TGMeasure replace){
		TGTrack track = TuxGuitar.instance().getSongManager().getTrack(this.trackNumber);
		if(track != null && replace != null){
			TGMeasureHeader header = TuxGuitar.instance().getSongManager().getMeasureHeader(replace.getNumber());
			TGMeasure measure = replace.clone(TuxGuitar.instance().getSongManager().getFactory(),header);
			measure = TuxGuitar.instance().getSongManager().getTrackManager().replaceMeasure(track,measure);
			TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().fireUpdate(measure.getNumber());
		}
	}
	
	public static UndoableMeasureGeneric startUndo( TGMeasure measure ){
		UndoableMeasureGeneric undoable = new UndoableMeasureGeneric();
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = measure.getTrack().getNumber();
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoMeasure = measure.clone(TuxGuitar.instance().getSongManager().getFactory(),measure.getHeader().clone(TuxGuitar.instance().getSongManager().getFactory()));
		return undoable;
	}
	
	public UndoableMeasureGeneric endUndo( TGMeasure measure ){
		this.redoCaret = new UndoableCaretHelper();
		this.redoMeasure = measure.clone(TuxGuitar.instance().getSongManager().getFactory(),measure.getHeader().clone(TuxGuitar.instance().getSongManager().getFactory()));
		return this;
	}
	
	public static UndoableMeasureGeneric startUndo(){
		/*
		UndoableMeasureGeneric undoable = new UndoableMeasureGeneric();
		Caret caret = getCaret();
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = caret.getTrack().getNumber();
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoMeasure = caret.getMeasure().clone(TuxGuitar.instance().getSongManager().getFactory(),caret.getMeasure().getHeader().clone(TuxGuitar.instance().getSongManager().getFactory()));
		return undoable;
		*/
		return startUndo( getCaret().getMeasure() );
	}
	
	public UndoableMeasureGeneric endUndo(){
		/*
		Caret caret = getCaret();
		this.redoCaret = new UndoableCaretHelper();
		this.redoMeasure = caret.getMeasure().clone(TuxGuitar.instance().getSongManager().getFactory(),caret.getMeasure().getHeader().clone(TuxGuitar.instance().getSongManager().getFactory()));
		return this;
		*/
		return endUndo( getCaret().getMeasure() );
	}
	
	private static Caret getCaret(){
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
	}
}
