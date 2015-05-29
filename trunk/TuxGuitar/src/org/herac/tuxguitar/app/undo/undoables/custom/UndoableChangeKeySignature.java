package org.herac.tuxguitar.app.undo.undoables.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableChangeKeySignature implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private long position;
	private int redoableKeySignature;
	private int undoableKeySignature;
	private List<KeySignaturePosition> nextKeySignaturePositions;
	private boolean toEnd;
	private TGTrack track;
	
	private UndoableChangeKeySignature(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TuxGuitar.getInstance().getSongManager().getTrackManager().changeKeySignature(this.track,this.position,this.redoableKeySignature,this.toEnd);
		TuxGuitar.getInstance().updateSong();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TuxGuitar.getInstance().getSongManager().getTrackManager().changeKeySignature(this.track,this.position,this.undoableKeySignature,this.toEnd);
		if(this.toEnd){
			Iterator<KeySignaturePosition> it = this.nextKeySignaturePositions.iterator();
			while(it.hasNext()){
				KeySignaturePosition ksp = (KeySignaturePosition)it.next();
				TuxGuitar.getInstance().getSongManager().getTrackManager().changeKeySignature(this.track,ksp.getPosition(),ksp.getKeySignature(),true);
			}
		}
		TuxGuitar.getInstance().updateSong();
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableChangeKeySignature startUndo(){
		UndoableChangeKeySignature undoable = new UndoableChangeKeySignature();
		Caret caret = getCaret();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.position = caret.getPosition();
		undoable.undoableKeySignature = caret.getMeasure().getKeySignature();
		undoable.track = caret.getTrack();
		undoable.nextKeySignaturePositions = new ArrayList<KeySignaturePosition>();
		
		int prevKeySignature = undoable.undoableKeySignature;
		Iterator<TGMeasure> it = caret.getTrack().getMeasures();
		while(it.hasNext()){
			TGMeasureImpl measure = (TGMeasureImpl)it.next();
			if(measure.getStart() > undoable.position){
				int currKeySignature = measure.getKeySignature();
				if(prevKeySignature != currKeySignature){
					KeySignaturePosition tsp = undoable.new KeySignaturePosition(measure.getStart(),currKeySignature);
					undoable.nextKeySignaturePositions.add(tsp);
				}
				prevKeySignature = currKeySignature;
			}
		}
		
		return undoable;
	}
	
	public UndoableChangeKeySignature endUndo(int keySignature,boolean toEnd){
		this.redoCaret = new UndoableCaretHelper();
		this.redoableKeySignature = keySignature;
		this.toEnd = toEnd;
		return this;
	}
	
	private static Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	private class KeySignaturePosition{
		private long position;
		private int keySignature;
		
		public KeySignaturePosition(long position,int keySignature) {
			this.position = position;
			this.keySignature = keySignature;
		}
		
		public long getPosition() {
			return this.position;
		}
		
		public int getKeySignature() {
			return this.keySignature;
		}
	}
}
