package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGSetTrackChannelAction extends TGActionBase {

	public static final String NAME = "action.track.set-channel";

	public TGSetTrackChannelAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGChannel channel = ((TGChannel) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL));
		if( track != null ){
			getSongManager(context).getTrackManager().changeChannel(track, channel);
		}
	}
}
