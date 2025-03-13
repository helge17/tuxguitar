package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.util.MidiTickUtil;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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