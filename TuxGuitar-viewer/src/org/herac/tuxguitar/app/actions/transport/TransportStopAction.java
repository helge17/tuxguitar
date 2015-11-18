/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.transport;

import java.awt.AWTEvent;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.util.MidiTickUtil;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

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
		TGSong song = getDocumentManager().getSong();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(song, MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition()));
		TuxGuitar.instance().getTransport().gotoMeasure(header, true);
	}
}
