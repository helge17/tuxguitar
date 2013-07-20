package org.herac.tuxguitar.app.actions.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;

public class TransportCountDownAction extends TGActionBase {
	
	public static final String NAME = "action.transport.count-down";
	
	public TransportCountDownAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TuxGuitar.instance().getPlayer().getCountDown().setEnabled(!TuxGuitar.instance().getPlayer().getCountDown().isEnabled());
	}
}
