/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.undo.CannotRedoException;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RedoAction extends TGActionBase{
	
	public static final String NAME = "action.edit.redo";
	
	public RedoAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		try {
			if(TuxGuitar.getInstance().getUndoableManager().canRedo()){
				TuxGuitar.getInstance().getUndoableManager().redo();
			}
		} catch (CannotRedoException e) {
			e.printStackTrace();
		}
	}
}
