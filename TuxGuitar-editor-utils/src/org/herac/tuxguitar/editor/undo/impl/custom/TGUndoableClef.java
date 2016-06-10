package org.herac.tuxguitar.editor.undo.impl.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableClef extends TGUndoableEditBase {
	
	private int doAction;
	private long position;
	private int redoableClef;
	private int undoableClef;
	private List<Object> nextClefPositions;
	private boolean toEnd;
	private TGTrack track;
	
	private TGUndoableClef(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeClef(actionContext, this.track, this.getMeasureAt(this.track, this.position), this.redoableClef, this.toEnd);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeClef(actionContext, this.track, this.getMeasureAt(this.track, this.position), this.undoableClef, this.toEnd);
		
		if(this.toEnd){
			Iterator<Object> it = this.nextClefPositions.iterator();
			while(it.hasNext()){
				ClefPosition ksp = (ClefPosition)it.next();
				this.changeClef(actionContext, this.track, this.getMeasureAt(this.track, ksp.getPosition()), ksp.getClef(), true);
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
	
	public static TGUndoableClef startUndo(TGContext context, TGTrack track, TGMeasure measure){
		TGUndoableClef undoable = new TGUndoableClef(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = measure.getStart();
		undoable.undoableClef = measure.getClef();
		undoable.track = track;
		undoable.nextClefPositions = new ArrayList<Object>();
		
		int prevClef = undoable.undoableClef;
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure nextMeasure = it.next();
			if(nextMeasure.getStart() > undoable.position){
				int currClef = nextMeasure.getClef();
				if(prevClef != currClef){
					ClefPosition tsp = new ClefPosition(nextMeasure.getStart(),currClef);
					undoable.nextClefPositions.add(tsp);
				}
				prevClef = currClef;
			}
		}
		
		return undoable;
	}
	
	public TGUndoableClef endUndo(int clef, boolean toEnd){
		this.redoableClef = clef;
		this.toEnd = toEnd;
		return this;
	}
	
	public TGMeasure getMeasureAt(TGTrack track, Long start) {
		return getSongManager().getTrackManager().getMeasureAt(track, start);
	}
	
	public void changeClef(TGActionContext context, TGTrack track, TGMeasure measure, Integer clef, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeClefAction.NAME);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, clef);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	private static class ClefPosition{
		private long position;
		private int clef;
		
		public ClefPosition(long position,int clef) {
			this.position = position;
			this.clef = clef;
		}
		
		public long getPosition() {
			return this.position;
		}
		
		public int getClef() {
			return this.clef;
		}
	}
}
