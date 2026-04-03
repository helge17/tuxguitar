package app.tuxguitar.editor.undo.impl.song;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUndoableSongGeneric extends TGUndoableEditBase {

	private int doAction;
	private TGSong undoSong;
	private TGSong redoSong;

	private TGUndoableSongGeneric(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.copySongFrom(actionContext, getSong(), this.redoSong);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.copySongFrom(actionContext, getSong(), this.undoSong);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableSongGeneric startUndo(TGContext context){
		TGUndoableSongGeneric undoable = new TGUndoableSongGeneric(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoSong = getSong(context).clone(new TGFactory());
		return undoable;
	}

	public TGUndoableSongGeneric endUndo(){
		this.redoSong = getSong(getContext()).clone(new TGFactory());
		return this;
	}

	public void copySongFrom(TGActionContext context, TGSong song, TGSong from) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCopySongFromAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGCopySongFromAction.ATTRIBUTE_FROM, from);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
