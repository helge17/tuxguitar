package org.herac.tuxguitar.android.action.listener.undoable;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableCaretState extends TGUndoableEditBase {
	
	private int doAction;
	private SelectionState undoableState;
	private SelectionState redoableState;
	
	public TGUndoableCaretState(TGContext context){
		super(context);
		
		this.startUndo();
	}
	
	public void startUndo(){
		this.doAction = UNDO_ACTION;
		this.undoableState = this.findCurrentState();
	}
	
	public void endUndo(){
		this.redoableState = this.findCurrentState();
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.updateCaret(this.redoableState);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.updateCaret(this.undoableState);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	private SelectionState findCurrentState() {
		TGCaret caret = getCaret();
		
		SelectionState state = new SelectionState();
		state.setTrack(caret.getTrack().getNumber());
		state.setPosition(caret.getPosition());
		state.setVelocity(caret.getVelocity());
		state.setDuration(caret.getDuration().clone(getSongManager().getFactory()));
		state.setString(1);
		TGString instrumentString = caret.getSelectedString();
		if(instrumentString != null){
			state.setString(instrumentString.getNumber());
		}
		
		return state;
	}
	
	private void updateCaret(SelectionState state){
		getCaret().update(state.getTrack(), state.getPosition(), state.getString(), state.getVelocity());
		getCaret().setSelectedDuration(state.getDuration().clone(getSongManager().getFactory()));
	}
	
	private TGCaret getCaret(){
		return TGSongViewController.getInstance(this.getContext()).getCaret();
	}
	
	private static class SelectionState {
		
		private long position;
		private int track;
		private int string;
		private int velocity;
		private TGDuration duration;
		
		public SelectionState() {
			super();
		}

		public long getPosition() {
			return position;
		}

		public void setPosition(long position) {
			this.position = position;
		}

		public int getTrack() {
			return track;
		}

		public void setTrack(int track) {
			this.track = track;
		}

		public int getString() {
			return string;
		}

		public void setString(int string) {
			this.string = string;
		}

		public int getVelocity() {
			return velocity;
		}

		public void setVelocity(int velocity) {
			this.velocity = velocity;
		}

		public TGDuration getDuration() {
			return duration;
		}

		public void setDuration(TGDuration duration) {
			this.duration = duration;
		}
	}
}
