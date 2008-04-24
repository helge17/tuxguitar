/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.caret;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableAddMeasure;

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
	
	protected int execute(TypedEvent e){
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
