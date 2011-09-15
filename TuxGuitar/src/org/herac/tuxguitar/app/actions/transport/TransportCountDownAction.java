package org.herac.tuxguitar.app.actions.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;

public class TransportCountDownAction extends Action {
	
	public static final String NAME = "action.transport.count-down";
	
	public TransportCountDownAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		TuxGuitar.instance().getPlayer().getCountDown().setEnabled(!TuxGuitar.instance().getPlayer().getCountDown().isEnabled());
		return 0;
	}
}
