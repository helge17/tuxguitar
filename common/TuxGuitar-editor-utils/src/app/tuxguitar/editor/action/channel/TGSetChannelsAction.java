package app.tuxguitar.editor.action.channel;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGSetChannelsAction extends TGActionBase{

	public static final String NAME = "action.channel.set-channels";

	public static final String ATTRIBUTE_CHANNELS = "channels";

	public TGSetChannelsAction(TGContext context) {
		super(context, NAME);
	}

	@SuppressWarnings("unchecked")
	protected void processAction(TGActionContext actionContext){
		TGActionManager actionManager = TGActionManager.getInstance(getContext());
		TGSongManager songManager = getSongManager(actionContext);
		TGSong song = ((TGSong) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		List<TGChannel> channels = ((List<TGChannel>) actionContext.getAttribute(ATTRIBUTE_CHANNELS));

		if( channels != null ) {
			songManager.removeAllChannels(song);

			for(TGChannel channel : channels){
				actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
				actionManager.execute(TGAddChannelAction.NAME, actionContext);
			}
		}
	}
}
