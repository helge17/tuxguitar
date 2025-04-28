package app.tuxguitar.editor.action.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.util.TGContext;

public class TGRedoAction extends TGActionBase {

	public static final String NAME = "action.edit.redo";

	public TGRedoAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		try {
			TGUndoableManager tgUndoableManager = TGUndoableManager.getInstance(this.getContext());
			if( tgUndoableManager.canRedo()){
				tgUndoableManager.redo(context);
			}
		} catch (TGCannotRedoException e) {
			throw new TGActionException(e);
		}
	}
}

