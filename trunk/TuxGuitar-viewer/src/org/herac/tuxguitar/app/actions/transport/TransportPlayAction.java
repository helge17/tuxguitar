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
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportPlayAction extends Action {
	public static final String NAME = "action.transport.play";
	
	public TransportPlayAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(AWTEvent e){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		if(!player.isRunning()){
			try{
				updateTransportPosition();
				player.getMode().reset();
				player.play();
				playThread();
			}catch(MidiPlayerException exception){
				exception.printStackTrace();
			}
		}else{
			player.pause();
			updateCaretPosition();
		}
		return 0;
	}
	
	protected void playThread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					while (TuxGuitar.instance().getPlayer().isRunning()) {
						if(TuxGuitar.instance().getPlayer().isRunning()){
							synchronized( TuxGuitar.instance() ){
								TuxGuitar.instance().redrawPayingMode();
								TuxGuitar.instance().wait(25);
							}
						}
					}
					updateCaretPosition();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		}).start();
	}
	
	protected void updateCaretPosition(){
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition()));
		TuxGuitar.instance().getTransport().gotoMeasure(header, true);
	}
	
	protected void updateTransportPosition(){
		TGMeasureHeader header = getEditor().getTablature().getCaret().getMeasure().getHeader();
		TuxGuitar.instance().getTransport().gotoMeasure(header, false);
	}
}