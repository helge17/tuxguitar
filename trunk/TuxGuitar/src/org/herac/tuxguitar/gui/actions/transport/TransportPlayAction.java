/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.transport;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.gui.util.MidiTickUtil;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGSynchronizer;

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
	
	protected int execute(TypedEvent e){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		if(!player.isRunning()){
			try{
				player.getMode().reset();
				player.play();
				playThread();
			}catch(MidiPlayerException exception){
				MessageDialog.errorMessage(exception);
			}
		}else{
			player.pause();
			updateTickPosition();
		}
		return 0;
	}
	
	protected void playThread() {
		final Display display = TuxGuitar.instance().getDisplay();
		final TGSynchronizer.TGRunnable playing = new TGSynchronizer.TGRunnable() {
			public void run() {
				if(TuxGuitar.instance().getPlayer().isRunning()){
					TuxGuitar.instance().redrawPayingMode();
				}
			}
		};
		final TGSynchronizer.TGRunnable finish = new TGSynchronizer.TGRunnable() {
			public void run() {
				updateTickPosition();
				TuxGuitar.instance().updateCache(true);
			}
		};
		new Thread(new Runnable() {
			public void run() {
				try {
					while (TuxGuitar.instance().getPlayer().isRunning()) {
						synchronized(playing){
							TGSynchronizer.instance().addRunnable(playing);
							playing.wait(25);
						}
					}
					if(!display.isDisposed()){
						TGSynchronizer.instance().addRunnable(finish);
					}
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		}).start();
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