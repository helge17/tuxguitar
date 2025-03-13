package app.tuxguitar.app.undo.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGUpdateMarkerAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUndoableMarkerGeneric extends TGUndoableEditBase {

	private int doAction;
	private TGMarker undoMarker;
	private TGMarker redoMarker;

	private TGUndoableMarkerGeneric(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		if(this.redoMarker != null){
			this.updateMarker(actionContext, getSong(), this.redoMarker.clone(getSongManager().getFactory()));
		}else if(this.undoMarker != null){
			this.removeMarker(actionContext, getSong(), this.undoMarker.clone(getSongManager().getFactory()));
		}
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		if(this.undoMarker != null){
			this.updateMarker(actionContext, getSong(), this.undoMarker.clone(getSongManager().getFactory()));
		}else if(this.redoMarker != null){
			this.removeMarker(actionContext, getSong(), this.redoMarker.clone(getSongManager().getFactory()));
		}
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableMarkerGeneric startUndo(TGContext context, TGMarker marker){
		TGUndoableMarkerGeneric undoable = new TGUndoableMarkerGeneric(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoMarker = (marker != null ? marker.clone(getSongManager(context).getFactory()) : null);

		return undoable;
	}

	public TGUndoableMarkerGeneric endUndo(TGMarker marker){
		this.redoMarker = (marker != null ? marker.clone(getSongManager().getFactory()) : null);

		return this;
	}

	public void updateMarker(TGActionContext context, TGSong song, TGMarker marker) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGUpdateMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}

	public void removeMarker(TGActionContext context, TGSong song, TGMarker marker) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRemoveMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
