package org.herac.tuxguitar.gui.undo.undoables.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
import org.herac.tuxguitar.gui.undo.undoables.UndoableCaretHelper;

public class UndoableChangeTripletFeel implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private long position;
	private int redoableTripletFeel;
	private int undoableTripletFeel;
	private List nextTripletFeelPositions;
	private boolean toEnd;
	
	private UndoableChangeTripletFeel(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TuxGuitar.instance().getSongManager().changeTripletFeel(this.position,this.redoableTripletFeel,this.toEnd);
		TuxGuitar.instance().fireUpdate();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		TuxGuitar.instance().getSongManager().changeTripletFeel(this.position,this.undoableTripletFeel,this.toEnd);
		if(this.toEnd){
			Iterator it = this.nextTripletFeelPositions.iterator();
			while(it.hasNext()){
				TripletFeelPosition tfp = (TripletFeelPosition)it.next();
				TuxGuitar.instance().getSongManager().changeTripletFeel(tfp.getPosition(),tfp.getTripletFeel(),true);
			}
		}
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
	
	public static UndoableChangeTripletFeel startUndo(){
		UndoableChangeTripletFeel undoable = new UndoableChangeTripletFeel();
		Caret caret = getCaret();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.position = caret.getPosition();
		undoable.undoableTripletFeel = caret.getMeasure().getTripletFeel();
		undoable.nextTripletFeelPositions = new ArrayList();
		
		int prevTripletFeel = undoable.undoableTripletFeel;
		Iterator it = TuxGuitar.instance().getSongManager().getFirstTrack().getMeasures();
		while(it.hasNext()){
			TGMeasureImpl measure = (TGMeasureImpl)it.next();
			if(measure.getStart() > undoable.position){
				int currTripletFeel = measure.getTripletFeel();
				if(prevTripletFeel != currTripletFeel){
					TripletFeelPosition tfp = undoable.new TripletFeelPosition(measure.getStart(),currTripletFeel);
					undoable.nextTripletFeelPositions.add(tfp);
				}
				prevTripletFeel = currTripletFeel;
			}
		}
		return undoable;
	}
	
	public UndoableChangeTripletFeel endUndo(int tripletFeel,boolean toEnd){
		this.redoCaret = new UndoableCaretHelper();
		this.redoableTripletFeel = tripletFeel;
		this.toEnd = toEnd;
		return this;
	}
	
	private static Caret getCaret(){
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
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
