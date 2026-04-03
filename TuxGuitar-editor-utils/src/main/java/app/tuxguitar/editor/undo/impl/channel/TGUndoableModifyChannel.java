package app.tuxguitar.editor.undo.impl.channel;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUndoableModifyChannel extends TGUndoableEditBase {

	private int doAction;
	private int channelId;
	private TGChannel undoChannel;
	private TGChannel redoChannel;

	private TGUndoableModifyChannel(TGContext context, int channelId){
		super(context);

		this.channelId = channelId;
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.updateChannel(actionContext, this.getSong(), this.redoChannel);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.updateChannel(actionContext, this.getSong(), this.undoChannel);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableModifyChannel startUndo(TGContext context, int channelId){
		TGUndoableModifyChannel undoable = new TGUndoableModifyChannel(context, channelId);
		undoable.doAction = UNDO_ACTION;
		undoable.undoChannel = undoable.cloneChannel(undoable.getChannel());
		return undoable;
	}

	public TGUndoableModifyChannel endUndo(){
		this.redoChannel = cloneChannel(getChannel());
		return this;
	}

	private TGChannel cloneChannel(TGChannel channel){
		return channel.clone(getSongManager().getFactory());
	}

	private TGChannel getChannel(){
		return getSongManager().getChannel(getSong(), this.channelId);
	}

	public void updateChannel(TGActionContext context, TGSong song, TGChannel channel) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGUpdateChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
