package app.tuxguitar.editor.undo.impl.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.channel.TGSetChannelsAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
