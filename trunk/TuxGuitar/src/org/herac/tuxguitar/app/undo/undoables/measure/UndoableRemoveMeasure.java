package org.herac.tuxguitar.app.undo.undoables.measure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.song.helpers.TGSongSegmentHelper;
import org.herac.tuxguitar.song.models.TGMarker;

public class UndoableRemoveMeasure implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGSongSegment tracksMeasures;
	private UndoMarkers undoMarkers;
	private int n1;
	private int n2;
	
	public UndoableRemoveMeasure(int n1,int n2){
		this.doAction = UNDO_ACTION;
		this.undoCaret = new UndoableCaretHelper();
		this.n1 = n1;
		this.n2 = n2;
		this.tracksMeasures = new TGSongSegmentHelper(TuxGuitar.getInstance().getSongManager()).copyMeasures(n1,n2);
		this.undoMarkers = new UndoMarkers();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		TuxGuitar.getInstance().getSongManager().removeMeasureHeaders(this.n1,this.n2);
		TuxGuitar.getInstance().updateSong();
		
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		new TGSongSegmentHelper(TuxGuitar.getInstance().getSongManager()).insertMeasures(this.tracksMeasures.clone(TuxGuitar.getInstance().getSongManager().getFactory()),this.n1,0,0);
		
		TuxGuitar.getInstance().updateSong();
		this.undoMarkers.undo();
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public UndoableRemoveMeasure endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		return this;
	}
	
	private class UndoMarkers{
		private List markers;
		
		public UndoMarkers(){
			this.markers = new ArrayList();
			Iterator it = TuxGuitar.getInstance().getSongManager().getMarkers().iterator();
			while(it.hasNext()){
				this.markers.add(((TGMarker)it.next()).clone(TuxGuitar.getInstance().getSongManager().getFactory()));
			}
		}
		
		public void undo(){
			TuxGuitar.getInstance().getSongManager().removeAllMarkers();
			Iterator it = this.markers.iterator();
			while(it.hasNext()){
				TGMarker marker = (TGMarker)it.next();
				TuxGuitar.getInstance().getSongManager().updateMarker(marker.clone(TuxGuitar.getInstance().getSongManager().getFactory()));
			}
		}
	}
}
