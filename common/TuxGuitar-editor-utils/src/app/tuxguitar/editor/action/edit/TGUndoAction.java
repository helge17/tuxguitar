package app.tuxguitar.editor.action.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.util.TGContext;

public class TGUndoAction extends TGActionBase {

	public static final String NAME = "action.edit.undo";

	public TGUndoAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		try {
			TGUndoableManager tgUndoableManager = TGUndoableManager.getInstance(this.getContext());
			if( tgUndoableManager.canUndo()){
				tgUndoableManager.undo(context);
			}
		} catch (TGCannotUndoException e) {
			throw new TGActionException(e);
		}
	}
}

