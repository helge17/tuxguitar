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

public class UndoableReplaceMeasures implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private UndoMarkers undoMarkers;
	private TGSongSegment undoTrackMeasures;
	private TGSongSegment redoTrackMeasures;
	private int toTrack;
	private int count;
	private int freeSpace;
	private long theMove;
	
	public UndoableReplaceMeasures(int p1,int p2,int toTrack){
		this.doAction = UNDO_ACTION;
		this.toTrack = toTrack;
		this.undoCaret = new UndoableCaretHelper();
		this.undoMarkers = new UndoMarkers();
		this.undoTrackMeasures = new TGSongSegmentHelper(TuxGuitar.getInstance().getSongManager()).copyMeasures(p1,p2);
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		for(int i = this.freeSpace;i < this.count;i ++){
			TuxGuitar.getInstance().getSongManager().addNewMeasureBeforeEnd();
		}
		new TGSongSegmentHelper(TuxGuitar.getInstance().getSongManager()).replaceMeasures(this.redoTrackMeasures.clone(TuxGuitar.getInstance().getSongManager().getFactory()),this.theMove,this.toTrack);
		
		TuxGuitar.getInstance().updateSong();
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		
		for(int i = this.freeSpace;i < this.count;i ++){
			TuxGuitar.getInstance().getSongManager().removeLastMeasure();
		}
		new TGSongSegmentHelper(TuxGuitar.getInstance().getSongManager()).replaceMeasures(this.undoTrackMeasures.clone(TuxGuitar.getInstance().getSongManager().getFactory()),0,0);
		
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
	
	public UndoableReplaceMeasures endUndo(TGSongSegment tracksMeasures,int count,int freeSpace,long theMove){
		this.redoCaret = new UndoableCaretHelper();
		this.redoTrackMeasures = tracksMeasures;
		this.count = count;
		this.freeSpace = freeSpace;
		this.theMove = theMove;
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
