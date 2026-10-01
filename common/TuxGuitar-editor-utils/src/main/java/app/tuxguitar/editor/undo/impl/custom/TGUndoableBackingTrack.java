package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGSetBackingTrackAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUndoableBackingTrack extends TGUndoableEditBase {

	private int doAction;
	private String undoPath;
	private String redoPath;

	private TGUndoableBackingTrack(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeBackingTrack(actionContext, getSong(), this.redoPath);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeBackingTrack(actionContext, getSong(), this.undoPath);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableBackingTrack startUndo(TGContext context){
		TGSong song = getSong(context);
		TGUndoableBackingTrack undoable = new TGUndoableBackingTrack(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoPath = song.getBackingTrack();
		return undoable;
	}

	public TGUndoableBackingTrack endUndo(){
		this.redoPath = getSong().getBackingTrack();
		return this;
	}

	public void changeBackingTrack(TGActionContext context, TGSong song, String path) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetBackingTrackAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_REMOVE, Boolean.TRUE);
		if( path != null ) {
			tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_PATH, path);
			tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_REMOVE, Boolean.FALSE);
		}
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
