package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateModifiedChannelController extends TGUpdateItemsController {
	
	public TGUpdateModifiedChannelController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		MidiPlayer midiPlayer = TuxGuitar.getInstance().getPlayer();
		midiPlayer.updateChannel((TGChannel) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL));
		
		// Call super update.
		super.update(context, actionContext);
	}
}
