package org.herac.tuxguitar.editor.undo.impl.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableKeySignature extends TGUndoableEditBase{
	
	private int doAction;
	private long position;
	private int redoableKeySignature;
	private int undoableKeySignature;
	private List<Object> nextKeySignaturePositions;
	private boolean toEnd;
	private TGTrack track;
	
	private TGUndoableKeySignature(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeKeySignature(actionContext, this.track, this.getMeasureAt(this.track, this.position), this.redoableKeySignature, this.toEnd);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeKeySignature(actionContext, this.track, this.getMeasureAt(this.track, this.position), this.undoableKeySignature, this.toEnd);
		
		if(this.toEnd){
			Iterator<Object> it = this.nextKeySignaturePositions.iterator();
			while(it.hasNext()){
				KeySignaturePosition ksp = (KeySignaturePosition)it.next();
				this.changeKeySignature(actionContext, this.track, this.getMeasureAt(this.track, ksp.getPosition()), ksp.getKeySignature(), true);
			}
		}
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableKeySignature startUndo(TGContext context, TGTrack track, TGMeasure measure) {
		TGUndoableKeySignature undoable = new TGUndoableKeySignature(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = measure.getStart();
		undoable.undoableKeySignature = measure.getKeySignature();
		undoable.track = track;
		undoable.nextKeySignaturePositions = new ArrayList<Object>();
		
		int prevKeySignature = undoable.undoableKeySignature;
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure nextMeasure = it.next();
			if( nextMeasure.getStart() > undoable.position ){
				int currKeySignature = nextMeasure.getKeySignature();
				if(prevKeySignature != currKeySignature){
					KeySignaturePosition tsp = new KeySignaturePosition(nextMeasure.getStart(), currKeySignature);
					undoable.nextKeySignaturePositions.add(tsp);
				}
				prevKeySignature = currKeySignature;
			}
		}
		
		return undoable;
	}
	
	public TGUndoableKeySignature endUndo(int keySignature, boolean toEnd){
		this.redoableKeySignature = keySignature;
		this.toEnd = toEnd;
		return this;
	}
	
	public TGMeasure getMeasureAt(TGTrack track, Long start) {
		return getSongManager().getTrackManager().getMeasureAt(track, start);
	}
	
	public void changeKeySignature(TGActionContext context, TGTrack track, TGMeasure measure, Integer keySignature, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeKeySignatureAction.NAME);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, keySignature);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	private static class KeySignaturePosition{
		
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
