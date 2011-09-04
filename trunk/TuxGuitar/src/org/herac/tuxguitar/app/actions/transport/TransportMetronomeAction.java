package org.herac.tuxguitar.app.actions.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;

public class TransportMetronomeAction extends Action {
	
	public static final String NAME = "action.transport.metronome";
	
	public TransportMetronomeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		TuxGuitar.instance().getPlayer().setMetronomeEnabled(!TuxGuitar.instance().getPlayer().isMetronomeEnabled());
		return 0;
	}
}
