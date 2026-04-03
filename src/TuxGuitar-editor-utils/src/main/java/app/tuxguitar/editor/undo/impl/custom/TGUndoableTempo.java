package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeTempoAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.util.TGContext;

public class TGUndoableTempo extends TGUndoableEditBase{

	private int doAction;
	private long position;
	private TGTempo undoableTempo;
	private TGTempo redoableTempo;

	private TGUndoableTempo(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeTempo(actionContext, this.getMeasureHeaderAt(this.position), this.redoableTempo);
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeTempo(actionContext, this.getMeasureHeaderAt(this.position), this.undoableTempo);
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableTempo startUndo(TGContext context, TGMeasureHeader header){
		TGUndoableTempo undoable = new TGUndoableTempo(context);
		undoable.doAction = UNDO_ACTION;
		undoable.position = header.getStart();
		undoable.undoableTempo = undoable.createTempoClone(header);
		return undoable;
	}

	public TGUndoableTempo endUndo(TGMeasureHeader header) {
		this.redoableTempo = createTempoClone(header);
		return this;
	}

	public TGMeasureHeader getMeasureHeaderAt(Long start) {
		return getSongManager().getMeasureHeaderAt(getSong(), start);
	}

	public TGTempo createTempoClone(TGMeasureHeader header) {
		return header.getTempo().clone(getSongManager().getFactory());
	}

	public void changeTempo(TGActionContext context, TGMeasureHeader header, TGTempo tempo) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeTempoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, tempo);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
