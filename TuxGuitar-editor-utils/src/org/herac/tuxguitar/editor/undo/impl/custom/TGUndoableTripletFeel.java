package org.herac.tuxguitar.editor.undo.impl.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTripletFeel extends TGUndoableEditBase {
	
	private int doAction;
	private long position;
	private int redoableTripletFeel;
	private int undoableTripletFeel;
	private List<Object> nextTripletFeelPositions;
	private boolean toEnd;
	
	private TGUndoableTripletFeel(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeTripletFeel(actionContext, getSong(), getMeasureHeaderAt(this.position), this.redoableTripletFeel, this.toEnd);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeTripletFeel(actionContext, getSong(), getMeasureHeaderAt(this.position), this.undoableTripletFeel, this.toEnd);
		
		if(this.toEnd){
			Iterator<Object> it = this.nextTripletFeelPositions.iterator();
			while(it.hasNext()){
				TripletFeelPosition tfp = (TripletFeelPosition)it.next();
				this.changeTripletFeel(actionContext, getSong(), getMeasureHeaderAt(tfp.getPosition()), tfp.getTripletFeel(), true);
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
	
	public static TGUndoableTripletFeel startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableTripletFeel undoable = new TGUndoableTripletFeel(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();
		undoable.undoableTripletFeel = header.getTripletFeel();
		undoable.nextTripletFeelPositions = new ArrayList<Object>();
		
		int prevTripletFeel = undoable.undoableTripletFeel;
		
		Iterator<TGMeasureHeader> it = getSong(context).getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader currHeader = it.next();
			if( currHeader.getStart() > undoable.position ){
				int currTripletFeel = currHeader.getTripletFeel();
				if( prevTripletFeel != currTripletFeel ){
					TripletFeelPosition tfp = undoable.new TripletFeelPosition(currHeader.getStart(), currTripletFeel);
					undoable.nextTripletFeelPositions.add(tfp);
				}
				prevTripletFeel = currTripletFeel;
			}
		}
		return undoable;
	}
	
	public TGUndoableTripletFeel endUndo(int tripletFeel,boolean toEnd){
		this.redoableTripletFeel = tripletFeel;
		this.toEnd = toEnd;
		return this;
	}
	
	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}
	
	public void changeTripletFeel(TGActionContext context, TGSong song, TGMeasureHeader header, Integer tripletFeel, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeTripletFeelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, tripletFeel);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	private class TripletFeelPosition{
		private long position;
		private int tripletFeel;
		
		public TripletFeelPosition(long position,int tripletFeel) {
			this.position = position;
			this.tripletFeel = tripletFeel;
		}
		public long getPosition() {
			return this.position;
		}
		public int getTripletFeel() {
			return this.tripletFeel;
		}
	}
}
