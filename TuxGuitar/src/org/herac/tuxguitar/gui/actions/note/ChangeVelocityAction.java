/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.note;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeVelocityAction extends Action{
	public static final String NAME = "action.note.general.velocity";
	
	public ChangeVelocityAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING);
	}
	
	protected int execute(TypedEvent e){
		if(e.widget.getData() instanceof Integer){
			int velocity = ((Integer)e.widget.getData()).intValue();
			
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			Caret caret = getEditor().getTablature().getCaret();
			caret.setVelocity(velocity);
			getSongManager().getMeasureManager().changeVelocity(velocity,caret.getMeasure(),caret.getPosition(),caret.getSelectedString().getNumber());
			updateTablature();
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
			
			return AUTO_UPDATE;
		}
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
