package org.herac.tuxguitar.app.action.impl.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;

public class TGBrowserAction extends TGActionBase{
	
	public static final String NAME = "action.tools.browser";
	
	public TGBrowserAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		if(TuxGuitar.instance().getBrowser().isDisposed()){
			TuxGuitar.instance().getBrowser().show();
		}else{
			TuxGuitar.instance().getBrowser().dispose();
		}
	}
}
