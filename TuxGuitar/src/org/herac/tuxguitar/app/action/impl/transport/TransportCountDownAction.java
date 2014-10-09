package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;

public class TransportCountDownAction extends TGActionBase {
	
	public static final String NAME = "action.transport.count-down";
	
	public TransportCountDownAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TuxGuitar.getInstance().getPlayer().getCountDown().setEnabled(!TuxGuitar.getInstance().getPlayer().getCountDown().isEnabled());
	}
}
