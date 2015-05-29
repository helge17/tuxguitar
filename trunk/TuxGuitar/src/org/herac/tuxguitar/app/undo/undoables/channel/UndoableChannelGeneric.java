package org.herac.tuxguitar.app.undo.undoables.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.CannotRedoException;
import org.herac.tuxguitar.app.undo.CannotUndoException;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.app.undo.undoables.UndoableCaretHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public class UndoableChannelGeneric implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private List<TGChannel> undoChannels;
	private List<TGChannel> redoChannels;
	
	private UndoableChannelGeneric(){
		super();
	}
	
	public void redo() throws CannotRedoException {
		if(!canRedo()){
			throw new CannotRedoException();
		}
		this.updateSongChannels(this.redoChannels);
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		if(!canUndo()){
			throw new CannotUndoException();
		}
		this.updateSongChannels(this.undoChannels);
		this.undoCaret.update();
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static UndoableChannelGeneric startUndo(){
		UndoableChannelGeneric undoable = new UndoableChannelGeneric();
		undoable.doAction = UNDO_ACTION;
		undoable.undoCaret = new UndoableCaretHelper();
		undoable.undoChannels = undoable.getChannels();
		return undoable;
	}
	
	public UndoableChannelGeneric endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		this.redoChannels = getChannels();
		return this;
	}
	
	private List<TGChannel> getChannels(){
		List<TGChannel> channels = new ArrayList<TGChannel>();
		Iterator<TGChannel> it = TuxGuitar.getInstance().getSongManager().getChannels(getSong()).iterator();
		while( it.hasNext() ){
			channels.add(cloneChannel((TGChannel)it.next()));
		}
		return channels;
	}
	
	private TGChannel cloneChannel(TGChannel channel){
		return channel.clone(TuxGuitar.getInstance().getSongManager().getFactory());
	}
	
	private void updateSongChannels( List<TGChannel> channels ){
		TGSongManager tgSongManager = TuxGuitar.getInstance().getSongManager();
		tgSongManager.removeAllChannels(getSong());
		
		Iterator<TGChannel> it = channels.iterator();
		while( it.hasNext() ){
			tgSongManager.addChannel(getSong(), cloneChannel((TGChannel)it.next()));
		}
		
		TuxGuitar.getInstance().updateCache(true);
	}
	
	private TGSong getSong() {
		return TuxGuitar.getInstance().getDocumentManager().getSong();
	}
}
