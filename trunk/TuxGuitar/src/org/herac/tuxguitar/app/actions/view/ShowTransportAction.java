/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.view;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.TGActionBase;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowTransportAction extends TGActionBase{
	
	public static final String NAME = "action.view.show-transport";
	
	public ShowTransportAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK |  AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		if(TuxGuitar.instance().getTransport().isDisposed()){
			TuxGuitar.instance().getTransport().show();
		}else{
			TuxGuitar.instance().getTransport().dispose();
		}
	}
}
