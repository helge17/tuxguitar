/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.transport;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.util.MidiTickUtil;
import org.herac.tuxguitar.player.base.MidiPlayer;
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
    	super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);   
    }
   
    protected int execute(TypedEvent e){
    	TuxGuitar.instance().getPlayer().reset();
    	updateTickPosition();
        return 0;
    }
    
    protected void updateTickPosition(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
    	TGMeasureHeader header = getSongManager().getMeasureHeaderAt(MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			player.setTickPosition(MidiTickUtil.getTick(header.getStart()));
		}
		getEditor().getTablature().getCaret().goToTickPosition();
    }
}
