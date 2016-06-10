package org.herac.tuxguitar.editor.action.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.util.TGContext;

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

