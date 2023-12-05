package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.MidiTickUtil;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TransportPlayAction extends TGActionBase {
	
	public static final String NAME = "action.transport.play";
	
	public TransportPlayAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
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
		TGDocumentManager documentManager = TGDocumentManager.getInstance(getContext());
		TGSong song = documentManager.getSong();
		TGMeasureHeader header = documentManager.getSongManager().getMeasureHeaderAt(song, MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition()));
		TuxGuitar.instance().getTransport().gotoMeasure(header, true);
	}
	
	protected void updateTransportPosition(){
		TGMeasureHeader header = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure().getHeader();
		TuxGuitar.instance().getTransport().gotoMeasure(header, false);
	}
}