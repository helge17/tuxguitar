package org.herac.tuxguitar.gui.actions.transport;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;

public class TransportMetronomeAction extends Action {
	
	public static final String NAME = "action.transport.metronome";
	
	public TransportMetronomeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		TuxGuitar.instance().getPlayer().setMetronomeEnabled(!TuxGuitar.instance().getPlayer().isMetronomeEnabled());
		return 0;
	}
}
