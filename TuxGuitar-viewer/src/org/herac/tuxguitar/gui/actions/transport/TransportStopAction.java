/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.transport;

import java.awt.AWTEvent;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.util.MidiTickUtil;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportStopAction extends Action{
	public static final String NAME = "action.transport.stop";
	
	public TransportStopAction() {
		super( NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(AWTEvent e){
		TuxGuitar.instance().getPlayer().reset();
		updateCaretPosition();
		return 0;
	}
	
	protected void updateCaretPosition(){
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition()));
		TuxGuitar.instance().getTransport().gotoMeasure(header, true);
	}
}
