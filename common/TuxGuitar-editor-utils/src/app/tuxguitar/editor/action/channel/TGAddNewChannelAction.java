package app.tuxguitar.editor.action.channel;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGAddNewChannelAction extends TGActionBase{

	public static final String NAME = "action.channel.add-new";

	public TGAddNewChannelAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		getSongManager(context).addChannel((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
	}
}
