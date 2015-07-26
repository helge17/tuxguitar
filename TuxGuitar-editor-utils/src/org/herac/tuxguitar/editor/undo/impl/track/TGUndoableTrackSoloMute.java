package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTrackSoloMute extends TGUndoableTrackBase {
	
	private int doAction;
	private int track;
	private boolean undoSolo;
	private boolean undoMute;
	private boolean redoSolo;
	private boolean redoMute;
	
	private TGUndoableTrackSoloMute(TGContext context){
		super(context);
	}
	
	public void redo() throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		if( this.undoSolo != this.redoSolo ){
			this.setTrackSolo(this.getTrack(this.track), this.redoSolo );
		}
		if( this.undoMute != this.redoMute ){
			this.setTrackMute(this.getTrack(this.track), this.redoMute );
		}
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		if( this.undoSolo != this.redoSolo ){
			this.setTrackSolo(this.getTrack(this.track), this.undoSolo );
		}
		if( this.undoMute != this.redoMute ){
			this.setTrackMute(this.getTrack(this.track), this.undoMute );
		}
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableTrackSoloMute startUndo(TGContext context, TGTrack track){
		TGUndoableTrackSoloMute undoable = new TGUndoableTrackSoloMute(context);
		undoable.doAction = UNDO_ACTION;
		undoable.track = track.getNumber();
		undoable.undoSolo = track.isSolo();
		undoable.undoMute = track.isMute();
		
		return undoable;
	}
	
	public TGUndoableTrackSoloMute endUndo(TGTrack track){
		this.redoSolo = track.isSolo();
		this.redoMute = track.isMute();
		
		return this;
	}
	
	public TGTrack getTrack(int number) {
		return getSongManager().getTrack(getSong(), number);
	}
}
