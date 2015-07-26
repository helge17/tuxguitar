package org.herac.tuxguitar.editor.action.channel;

import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
