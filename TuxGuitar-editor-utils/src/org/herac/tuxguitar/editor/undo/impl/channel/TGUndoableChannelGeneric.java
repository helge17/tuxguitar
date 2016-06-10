package org.herac.tuxguitar.editor.undo.impl.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.channel.TGSetChannelsAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableChannelGeneric extends TGUndoableEditBase {
	
	private int doAction;
	private List<TGChannel> undoChannels;
	private List<TGChannel> redoChannels;
	
	private TGUndoableChannelGeneric(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.setChannels(actionContext, this.getSong(), this.cloneChannels(getSongManager().getFactory(), this.redoChannels));
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.setChannels(actionContext, this.getSong(), this.cloneChannels(getSongManager().getFactory(), this.undoChannels));
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableChannelGeneric startUndo(TGContext context){
		TGUndoableChannelGeneric undoable = new TGUndoableChannelGeneric(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoChannels = undoable.getClonedChannels();
		return undoable;
	}
	
	public TGUndoableChannelGeneric endUndo(){
		this.redoChannels = this.getClonedChannels();
		return this;
	}
	
	private List<TGChannel> getClonedChannels() {
		List<TGChannel> channels = new ArrayList<TGChannel>();
		Iterator<?> it = getSongManager().getChannels(getSong()).iterator();
		while( it.hasNext() ){
			channels.add((TGChannel)it.next());
		}
		return this.cloneChannels(new TGFactory(), channels);
	}
	
	public List<TGChannel> cloneChannels(TGFactory factory, List<TGChannel> channels) {
		List<TGChannel> clonedChannels = new ArrayList<TGChannel>();
		for(TGChannel channel : channels) {
			clonedChannels.add(channel.clone(factory));
		}
		return clonedChannels;
	}
	
	public void setChannels(TGActionContext context, TGSong song, List<TGChannel> channels) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetChannelsAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGSetChannelsAction.ATTRIBUTE_CHANNELS, channels);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
