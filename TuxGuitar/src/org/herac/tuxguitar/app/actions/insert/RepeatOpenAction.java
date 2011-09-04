/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.insert;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.undo.undoables.custom.UndoableChangeOpenRepeat;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RepeatOpenAction extends Action{
	
	public static final String NAME = "action.insert.open-repeat";
	
	public RepeatOpenAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		//comienza el undoable
		UndoableChangeOpenRepeat undoable = UndoableChangeOpenRepeat.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		TGMeasureImpl measure = getEditor().getTablature().getCaret().getMeasure();
		getSongManager().changeOpenRepeat(measure.getStart());
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
