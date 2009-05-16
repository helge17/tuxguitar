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
	private int pasteCount;
	
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
		skipMarkers(this.segment);
	}
	
	public void insertTransfer() throws CannotInsertTransferException {
		TGSongSegmentHelper helper = new TGSongSegmentHelper(TuxGuitar.instance().getSongManager());
		TGSongSegment segment = helper.createSegmentCopies(this.segment, this.pasteCount );
		if(this.transferType == TRANSFER_TYPE_REPLACE){
			replaceMeasures(helper, segment);
		}else if(this.transferType == TRANSFER_TYPE_INSERT){
			insertMeasures(helper, segment);
		}
	}
	
	public void insertMeasures(TGSongSegmentHelper helper, TGSongSegment segment) throws CannotInsertTransferException {
		TGMeasure measure = this.tablatureEditor.getTablature().getCaret().getMeasure();
		TGTrack track = this.tablatureEditor.getTablature().getCaret().getTrack();
		if (measure == null || segment.isEmpty()) {
			throw new CannotInsertTransferException();
		}
		//Si el segmento tiene una sola pista,
		//la pego en la pista seleccionada
		int toTrack = ((segment.getTracks().size() == 1)?track.getNumber():0);
		
		//comienza el undoable
		UndoableInsertMeasure undoable = new UndoableInsertMeasure(toTrack);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		TGMeasureHeader first = (TGMeasureHeader)segment.getHeaders().get(0);
		int fromNumber = measure.getNumber();
		long theMove = (measure.getStart() - first.getStart());
		
		helper.insertMeasures(segment.clone(TuxGuitar.instance().getSongManager().getFactory()),fromNumber,theMove,toTrack);
		
		//termia el undoable
		TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(segment.clone(TuxGuitar.instance().getSongManager().getFactory()),segment.getHeaders().size(),fromNumber,theMove));
	}
	
	public void replaceMeasures(TGSongSegmentHelper helper, TGSongSegment segment) throws CannotInsertTransferException {
		TGMeasure measure = this.tablatureEditor.getTablature().getCaret().getMeasure();
		TGTrack track = this.tablatureEditor.getTablature().getCaret().getTrack();
		if (measure == null || segment.isEmpty()) {
			throw new CannotInsertTransferException();
		}
		TGMeasureHeader first = (TGMeasureHeader)segment.getHeaders().get(0);
		
		//Si el segmento tiene una sola pista,
		//la pego en la pista seleccionada
		int toTrack = ((segment.getTracks().size() == 1)?track.getNumber():0);
		
		//si no existen los compases los creo
		int count = segment.getHeaders().size();
		int current = measure.getNumber();
		int freeSpace =  (track.countMeasures() - (current - 1));
		long theMove = (measure.getStart() - first.getStart());
		
		//comienza el undoable
		UndoableReplaceMeasures undoable = new UndoableReplaceMeasures(current, (current + count) , toTrack);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		for(int i = freeSpace;i < count;i ++){
			TuxGuitar.instance().getSongManager().addNewMeasureBeforeEnd();
		}
		helper.replaceMeasures(segment.clone(TuxGuitar.instance().getSongManager().getFactory()),theMove,toTrack);
		
		//Termina el undoable
		TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(segment.clone(TuxGuitar.instance().getSongManager().getFactory()),count,freeSpace,theMove));
	}
	
	public void setTransferType(int transferType){
		this.transferType = transferType;
	}
	
	public void setPasteCount(int pasteCount){
		this.pasteCount = pasteCount;
	}
	
	private void skipMarkers(TGSongSegment segment){
		Iterator it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			header.setMarker(null);
		}
	}
}