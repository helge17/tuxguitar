package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGMeasure;

public class TransportSetLoopSHeaderAction extends TGActionBase {
	
	public static final String NAME = "action.transport.set-loop-start";
	
	public TransportSetLoopSHeaderAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TGMeasure measure = getEditor().getTablature().getCaret().getMeasure();
		if( measure != null ){
			MidiPlayerMode pm = TuxGuitar.getInstance().getPlayer().getMode();
			if( pm.isLoop() ){
				pm.setLoopSHeader( pm.getLoopSHeader() != measure.getNumber() ? measure.getNumber() : -1 );
			}
		}
	}
}
