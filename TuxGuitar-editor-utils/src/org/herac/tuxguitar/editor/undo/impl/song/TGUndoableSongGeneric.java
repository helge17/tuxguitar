package org.herac.tuxguitar.editor.undo.impl.song;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.song.TGCopySongFromAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableSongGeneric extends TGUndoableEditBase {
	
	private int doAction;
	private TGSong undoSong;
	private TGSong redoSong;
	
	private TGUndoableSongGeneric(TGContext context){
		super(context);
	}
	
	public void redo() throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.copySongFrom(getSong(), this.redoSong);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.copySongFrom(getSong(), this.undoSong);
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
	
	public void copySongFrom(TGSong song, TGSong from) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCopySongFromAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGCopySongFromAction.ATTRIBUTE_FROM, from);
		this.processByPassUndoableAction(tgActionProcessor);
	}
}
