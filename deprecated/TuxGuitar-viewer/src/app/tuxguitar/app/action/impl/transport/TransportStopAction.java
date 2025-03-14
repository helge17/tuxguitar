package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.util.MidiTickUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TransportStopAction extends TGActionBase {

	public static final String NAME = "action.transport.stop";

	public TransportStopAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TuxGuitar.instance().getPlayer().reset();
		updateCaretPosition(context);
	}

	protected void updateCaretPosition(TGActionContext context){
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGMeasureHeader header = getSongManager(context).getMeasureHeaderAt(song, MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition()));
		TuxGuitar.instance().getTransport().gotoMeasure(header, true);
	}
}
