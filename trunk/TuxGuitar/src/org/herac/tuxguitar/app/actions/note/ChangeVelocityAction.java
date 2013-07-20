/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeVelocityAction extends TGActionBase{
	
	public static final String NAME = "action.note.general.velocity";
	
	public static final String PROPERTY_VELOCITY = "velocity";
	
	public ChangeVelocityAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING);
	}
	
	protected void processAction(TGActionContext context){
		Object propertyVelocity = context.getAttribute(PROPERTY_VELOCITY);
		if( propertyVelocity instanceof Integer){
			int velocity = ((Integer)propertyVelocity).intValue();
			
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			Caret caret = getEditor().getTablature().getCaret();
			caret.setVelocity(velocity);
			getSongManager().getMeasureManager().changeVelocity(velocity,caret.getMeasure(),caret.getPosition(),caret.getSelectedString().getNumber());
			updateTablature();
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
