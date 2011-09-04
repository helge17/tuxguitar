/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.edit;

import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.editors.tab.edit.EditorKit;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMouseModeSelectionAction extends Action{
	
	public static final String NAME = "action.edit.set-mouse-mode-selection";
	
	public SetMouseModeSelectionAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING | AUTO_UPDATE);
	}
	
	protected int execute(ActionData actionData){
		getEditor().getTablature().getEditorKit().setMouseMode(EditorKit.MOUSE_MODE_SELECTION);
		return 0;
	}
}
