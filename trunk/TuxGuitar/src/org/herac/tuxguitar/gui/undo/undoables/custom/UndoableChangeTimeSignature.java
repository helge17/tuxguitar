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
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;

public class UndoableChangeTimeSignature implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;		
	private long position;		
	private TGTimeSignature redoableTimeSignature;
	private TGTimeSignature undoableTimeSignature;
	private List nextTimeSignaturePositions;
	private boolean toEnd;
	
	private UndoableChangeTimeSignature(){
		super();
	}
	
	public void redo() throws CannotRedoException {	
		if(!canRedo()){
			throw new CannotRedoException();
		}        
		TuxGuitar.instance().getSongManager().changeTimeSignature(this.position,this.redoableTimeSignature.clone(TuxGuitar.instance().getSongManager().getFactory()),this.toEnd);
		TuxGuitar.instance().fireUpdate();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}

	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}	

		TuxGuitar.instance().getSongManager().changeTimeSignature(this.position,this.undoableTimeSignature.clone(TuxGuitar.instance().getSongManager().getFactory()),this.toEnd);
				
		if(this.toEnd){
			Iterator it = this.nextTimeSignaturePositions.iterator();
			while(it.hasNext()){		
				TimeSignaturePosition tsp = (TimeSignaturePosition)it.next();
				TuxGuitar.instance().getSongManager().changeTimeSignature(tsp.getPosition(),tsp.getTimeSignature().clone(TuxGuitar.instance().getSongManager().getFactory()),true);
			}
		}
		
		if( shouldRemoveRightBeats() ){
			removeRightBeats( this.position, this.toEnd );
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
      
    public static UndoableChangeTimeSignature startUndo(){
    	UndoableChangeTimeSignature undoable = new UndoableChangeTimeSignature();
    	Caret caret = getCaret();    	    	
    	undoable.doAction = UNDO_ACTION;
    	undoable.undoCaret = new UndoableCaretHelper();
    	undoable.position = caret.getPosition();
    	undoable.undoableTimeSignature = caret.getMeasure().getTimeSignature().clone(TuxGuitar.instance().getSongManager().getFactory());
    	undoable.nextTimeSignaturePositions = new ArrayList();    	
    	
    	TGTimeSignature prevTimeSignature = undoable.undoableTimeSignature;
    	Iterator it = TuxGuitar.instance().getSongManager().getFirstTrack().getMeasures();
    	while(it.hasNext()){
    		TGMeasureImpl measure = (TGMeasureImpl)it.next();
    		if(measure.getStart() > undoable.position){
    			TGTimeSignature currTimeSignature = measure.getTimeSignature();
                int numerator = prevTimeSignature.getNumerator();
                int value = prevTimeSignature.getDenominator().getValue();
                int currNumerator = currTimeSignature.getNumerator();
                int currValue = currTimeSignature.getDenominator().getValue();            
                if(numerator != currNumerator || value != currValue){
                	TimeSignaturePosition tsp = undoable.new TimeSignaturePosition(measure.getStart(),currTimeSignature.clone(TuxGuitar.instance().getSongManager().getFactory()));
                	undoable.nextTimeSignaturePositions.add(tsp);
                }    			
                prevTimeSignature = currTimeSignature;
    		}
    	}
    	
    	return undoable;
    }
    
    public UndoableChangeTimeSignature endUndo(TGTimeSignature timeSignature,boolean toEnd){
    	this.redoCaret = new UndoableCaretHelper();
    	this.redoableTimeSignature = timeSignature.clone(TuxGuitar.instance().getSongManager().getFactory());
    	this.toEnd = toEnd;
    	return this;
    }
    
    private static Caret getCaret(){
    	return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
    }  
    
    private boolean shouldRemoveRightBeats(){
    	long redoableTime = (this.redoableTimeSignature.getNumerator() * this.redoableTimeSignature.getDenominator().getTime());
    	long undoableTime = (this.undoableTimeSignature.getNumerator() * this.undoableTimeSignature.getDenominator().getTime());
    	return (redoableTime > undoableTime);
    }

    private void removeRightBeats(long position, boolean toEnd){
    	TGSongManager manager = TuxGuitar.instance().getSongManager();
    	int tracks = manager.getSong().countTracks();
    	for( int i = 1 ; i <= tracks ; i ++ ){
    		TGTrack track = manager.getTrack( i );
    		TGMeasure measure = manager.getTrackManager().getMeasureAt(track, position );
    		
    		manager.getMeasureManager().removeBeatsBeforeEnd(measure, ( measure.getStart() + measure.getLength() ) );
    		
    		if( toEnd ){
    			while( (measure = manager.getTrackManager().getNextMeasure(measure)) != null ){
    				manager.getMeasureManager().removeBeatsBeforeEnd(measure, ( measure.getStart() + measure.getLength() ) );
    			}
    		}
    	}
    }    
    
    private class TimeSignaturePosition{
    	private long position;
    	private TGTimeSignature timeSignature;
    	
		public TimeSignaturePosition(long position, TGTimeSignature timeSignature) {
			this.position = position;
			this.timeSignature = timeSignature;
		}
		public long getPosition() {
			return this.position;
		}
		public TGTimeSignature getTimeSignature() {
			return this.timeSignature;
		}    	
    }
}
