/*
 * Created on 09-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.clipboard;

import java.util.Iterator;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableInsertMeasure;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableReplaceMeasures;
import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.song.helpers.TGSongSegmentHelper;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class MeasureTransferable implements Transferable {
	public static final int TRANSFER_TYPE_REPLACE = 1;
	public static final int TRANSFER_TYPE_INSERT = 2;
	
    private TablatureEditor tablatureEditor;
    private TGSongSegment segment;
    private int transferType;
    
    public MeasureTransferable(TablatureEditor tablatureEditor, int p1, int p2,boolean allTracks) {
        this.tablatureEditor = tablatureEditor;
        this.transferType = TRANSFER_TYPE_REPLACE;
        this.getTransfer(p1, p2,allTracks);
    }

    private void getTransfer(int p1, int p2,boolean allTracks) {
    	if(allTracks){
    		this.segment = new TGSongSegmentHelper(TuxGuitar.instance().getSongManager()).copyMeasures(p1,p2);
    	}else{
    		TGTrack track = this.tablatureEditor.getTablature().getCaret().getTrack();
    		this.segment = new TGSongSegmentHelper(TuxGuitar.instance().getSongManager()).copyMeasures(p1,p2,track);
    	}
    	skipMarkers();
    }
    
    public void insertTransfer() throws CannotInsertTransferException {
        if(this.transferType == TRANSFER_TYPE_REPLACE){
        	replaceMeasures();
        }else if(this.transferType == TRANSFER_TYPE_INSERT){
        	insertMeasures();
        }
    }
    
    public void insertMeasures() throws CannotInsertTransferException {
        TGMeasure measure = this.tablatureEditor.getTablature().getCaret().getMeasure();
        TGTrack track = this.tablatureEditor.getTablature().getCaret().getTrack();
        if (measure == null || this.segment.isEmpty()) {
            throw new CannotInsertTransferException();
        }                        
        //Si el segmento tiene una sola pista, 
        //la pego en la pista seleccionada
        int toTrack = ((this.segment.getTracks().size() == 1)?track.getNumber():0);        
        
        //comienza el undoable
        UndoableInsertMeasure undoable = new UndoableInsertMeasure(toTrack);
        TuxGuitar.instance().getFileHistory().setUnsavedFile();
        
        TGMeasureHeader first = (TGMeasureHeader)this.segment.getHeaders().get(0);
        int fromNumber = measure.getNumber();
        long theMove = (measure.getStart() - first.getStart());
        
        new TGSongSegmentHelper(TuxGuitar.instance().getSongManager()).insertMeasures(this.segment.clone(TuxGuitar.instance().getSongManager().getFactory()),fromNumber,theMove,toTrack);
        
        //termia el undoable        
        TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(this.segment.clone(TuxGuitar.instance().getSongManager().getFactory()),this.segment.getHeaders().size(),fromNumber,theMove));          
    }    

    public void replaceMeasures() throws CannotInsertTransferException {        
        TGMeasure measure = this.tablatureEditor.getTablature().getCaret().getMeasure();
        TGTrack track = this.tablatureEditor.getTablature().getCaret().getTrack();
        if (measure == null || this.segment.isEmpty()) {
            throw new CannotInsertTransferException();
        }      
        TGMeasureHeader first = (TGMeasureHeader)this.segment.getHeaders().get(0);        
        
        //Si el segmento tiene una sola pista, 
        //la pego en la pista seleccionada
        int toTrack = ((this.segment.getTracks().size() == 1)?track.getNumber():0);
        
        //si no existen los compases los creo        
        int count = this.segment.getHeaders().size();        
        int current = measure.getNumber();
        int freeSpace =  (track.countMeasures()  - (current - 1));
        long theMove = (measure.getStart() - first.getStart());
        
        //comienza el undoable
        UndoableReplaceMeasures undoable = new UndoableReplaceMeasures(current, (current + count) , toTrack);          
        TuxGuitar.instance().getFileHistory().setUnsavedFile();
        
        for(int i = freeSpace;i < count;i ++){
        	TuxGuitar.instance().getSongManager().addNewMeasureBeforeEnd();
        }
        new TGSongSegmentHelper(TuxGuitar.instance().getSongManager()).replaceMeasures(this.segment.clone(TuxGuitar.instance().getSongManager().getFactory()),theMove,toTrack);
        
        //Termina el undoable
        TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(this.segment.clone(TuxGuitar.instance().getSongManager().getFactory()),count,freeSpace,theMove));
    }        
    
    public void setTransferType(int transferType){
    	this.transferType = transferType;
    }

    private void skipMarkers(){
    	Iterator it = this.segment.getHeaders().iterator();
    	while(it.hasNext()){
    		TGMeasureHeader header = (TGMeasureHeader)it.next();
    		header.setMarker(null);
    	}
    }
}