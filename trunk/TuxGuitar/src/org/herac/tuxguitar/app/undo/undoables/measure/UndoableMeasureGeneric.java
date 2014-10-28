package org.herac.tuxguitar.app.undo.undoables.measure;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
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
		TGTrack track = TuxGuitar.getInstance().getSongManager().getTrack(getSong(), this.trackNumber);
		if(track != null && replace != null){
			TGMeasureHeader header = TuxGuitar.getInstance().getSongManager().getMeasureHeader(getSong(), replace.getNumber());
			TGMeasure measure = replace.clone(TuxGuitar.getInstance().getSongManager().getFactory(),header);
			TuxGuitar.getInstance().getSongManager().getTrackManager().replaceMeasure(track,measure);
			TuxGuitar.getInstance().getTablatureEditor().getTablature().updateMeasure(header.getNumber());
		}
	}
	
	public static UndoableMeasureGeneric startUndo( TGMeasure measure ){
		UndoableMeasureGeneric undoable = new UndoableMeasureGeneric();
		undoable.doAction = UNDO_ACTION;
		undoable.trackNumber = measure.getTrack().getNumber();
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoMeasure = measure.clone(TuxGuitar.getInstance().getSongManager().getFactory(),measure.getHeader().clone(TuxGuitar.getInstance().getSongManager().getFactory()));
		return undoable;
	}
	
	public UndoableMeasureGeneric endUndo( TGMeasure measure ){
		this.redoCaret = new UndoableCaretHelper();
		this.redoMeasure = measure.clone(TuxGuitar.getInstance().getSongManager().getFactory(),measure.getHeader().clone(TuxGuitar.getInstance().getSongManager().getFactory()));
		return this;
	}
	
	public static UndoableMeasureGeneric startUndo(){
		return startUndo( getCaret().getMeasure() );
	}
	
	public UndoableMeasureGeneric endUndo(){
		return endUndo( getCaret().getMeasure() );
	}
	
	private static Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	public TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
