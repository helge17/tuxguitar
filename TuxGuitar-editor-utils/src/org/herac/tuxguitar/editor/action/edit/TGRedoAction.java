package org.herac.tuxguitar.editor.action.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.util.TGContext;

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

