package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
