package org.herac.tuxguitar.app.undo.undoables.channel;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;

public class UndoableModifyChannel implements UndoableEdit{
	
	private int channelId;
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private TGChannel undoChannel;
	private TGChannel redoChannel;
	
	private UndoableModifyChannel(int channelId){
		this.channelId = channelId;
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		this.updateSongChannel(this.redoChannel);
		this.redoCaret.update();
		
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		this.updateSongChannel(this.undoChannel);
		this.undoCaret.update();
		
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableModifyChannel startUndo(int channelId){
		UndoableModifyChannel undoable = new UndoableModifyChannel(channelId);
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoChannel = undoable.cloneChannel(undoable.getChannel());
		return undoable;
	}
	
	public UndoableModifyChannel endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		this.redoChannel = cloneChannel(getChannel());
		return this;
	}
	
	private TGChannel cloneChannel(TGChannel channel){
		return channel.clone(TuxGuitar.instance().getSongManager().getFactory());
	}
	
	private TGChannel getChannel(){
		return TuxGuitar.instance().getSongManager().getChannel(this.channelId);
	}
	
	private void updateSongChannel(TGChannel channel){
		TGSongManager tgSongManager = TuxGuitar.instance().getSongManager();
		tgSongManager.updateChannel(
			channel.getChannelId(), 
			channel.getChannel(), 
			channel.getEffectChannel(), 
			channel.getBank(), 
			channel.getProgram(), 
			channel.getVolume(), 
			channel.getBalance(), 
			channel.getChorus(), 
			channel.getReverb(), 
			channel.getPhaser(), 
			channel.getTremolo(), 
			channel.getName()
		);
		
		TuxGuitar.instance().updateCache(true);
	}
}
