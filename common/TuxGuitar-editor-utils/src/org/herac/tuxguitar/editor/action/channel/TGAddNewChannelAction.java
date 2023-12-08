package org.herac.tuxguitar.editor.action.channel;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGAddNewChannelAction extends TGActionBase{
	
	public static final String NAME = "action.channel.add-new";
	
	public TGAddNewChannelAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		getSongManager(context).addChannel((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
	}
}
