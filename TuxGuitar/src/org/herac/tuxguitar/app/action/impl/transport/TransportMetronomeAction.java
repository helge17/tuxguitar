package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;

public class TransportMetronomeAction extends TGActionBase {
	
	public static final String NAME = "action.transport.metronome";
	
	public TransportMetronomeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TuxGuitar.instance().getPlayer().setMetronomeEnabled(!TuxGuitar.instance().getPlayer().isMetronomeEnabled());
	}
}
