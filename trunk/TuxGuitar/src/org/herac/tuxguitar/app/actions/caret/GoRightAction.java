/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.caret;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableAddMeasure;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoRightAction extends Action{
	
	public static final String NAME = "action.caret.go-right";
	
	public GoRightAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE);
	}
	
	protected int execute(ActionData actionData){
		if(TuxGuitar.instance().getPlayer().isRunning()){
			TuxGuitar.instance().getTransport().gotoNext();
		}
		else{
			Caret caret = getEditor().getTablature().getCaret();
			if(!caret.moveRight()){
				int number = (getSongManager().getSong().countMeasureHeaders() + 1);
				
				//comienza el undoable
				UndoableAddMeasure undoable = UndoableAddMeasure.startUndo(number);
				
				getSongManager().addNewMeasure(number);
				fireUpdate(number);
				caret.moveRight();
				
				TuxGuitar.instance().getFileHistory().setUnsavedFile();
				
				//termia el undoable
				addUndoableEdit(undoable.endUndo());
			}
		}
		return 0;
	}
}
