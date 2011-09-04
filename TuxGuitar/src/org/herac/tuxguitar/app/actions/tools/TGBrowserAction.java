package org.herac.tuxguitar.app.actions.tools;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;

public class TGBrowserAction extends Action{
	
	public static final String NAME = "action.tools.browser";
	
	public TGBrowserAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		if(TuxGuitar.instance().getBrowser().isDisposed()){
			TuxGuitar.instance().getBrowser().show();
		}else{
			TuxGuitar.instance().getBrowser().dispose();
		}
		return 0;
	}
}
