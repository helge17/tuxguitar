package org.herac.tuxguitar.app.actions.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGMeasure;

public class TransportSetLoopSHeaderAction extends Action {
	
	public static final String NAME = "action.transport.set-loop-start";
	
	public TransportSetLoopSHeaderAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		TGMeasure measure = getEditor().getTablature().getCaret().getMeasure();
		if( measure != null ){
			MidiPlayerMode pm = TuxGuitar.instance().getPlayer().getMode();
			if( pm.isLoop() ){
				pm.setLoopSHeader( pm.getLoopSHeader() != measure.getNumber() ? measure.getNumber() : -1 );
			}
		}
		return 0;
	}
}
