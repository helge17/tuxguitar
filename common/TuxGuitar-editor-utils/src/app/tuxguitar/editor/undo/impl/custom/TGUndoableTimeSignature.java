package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.util.TGContext;

public class TGUndoableTimeSignature extends TGUndoableEditBase {

	private int doAction;
	private TGSong song;
	private long tsStart;
	private boolean tsToEnd;
	private TGTimeSignature ts;

	private TGUndoableTimeSignature(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeTimeSignature(actionContext, getSong(), this.getMeasureHeaderAt(this.tsStart), this.ts, this.tsToEnd);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.copySongFrom(actionContext, getSong(), this.song);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableTimeSignature startUndo(TGContext context){
		TGFactory factory = new TGFactory();
		TGSong song = getSong(context);
		TGUndoableTimeSignature undoable = new TGUndoableTimeSignature(context);
		undoable.doAction = UNDO_ACTION;
		undoable.song = song.clone(factory);
		return undoable;
	}

	public TGUndoableTimeSignature endUndo(TGTimeSignature timeSignature,long start, boolean toEnd){
		this.ts = timeSignature;
		this.tsStart = start;
		this.tsToEnd = toEnd;
		return this;
	}

	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}

	public void changeTimeSignature(TGActionContext context, TGSong song, TGMeasureHeader header, TGTimeSignature timeSignature, Boolean toEnd) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, timeSignature);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, toEnd);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}

	public void copySongFrom(TGActionContext context, TGSong song, TGSong from) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCopySongFromAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGCopySongFromAction.ATTRIBUTE_FROM, from);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
