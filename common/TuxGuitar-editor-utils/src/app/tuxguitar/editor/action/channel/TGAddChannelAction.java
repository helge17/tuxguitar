package app.tuxguitar.editor.action.channel;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGAddChannelAction extends TGActionBase{

	public static final String NAME = "action.channel.add";

	public TGAddChannelAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGChannel channel = ((TGChannel) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL));

		getSongManager(context).addChannel(song, channel);
	}
}
