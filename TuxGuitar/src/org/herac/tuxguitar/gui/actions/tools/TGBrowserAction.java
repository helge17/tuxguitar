package org.herac.tuxguitar.gui.actions.tools;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;

public class TGBrowserAction extends Action{
	public static final String NAME = "action.tools.browser";
	
	public TGBrowserAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		if(TuxGuitar.instance().getBrowser().isDisposed()){
			TuxGuitar.instance().getBrowser().show();
		}else{
			TuxGuitar.instance().getBrowser().dispose();
		}
		return 0;
	}
}
