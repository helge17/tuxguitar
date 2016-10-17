package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateModifiedChannelController extends TGUpdateItemsController {
	
	public TGUpdateModifiedChannelController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		if( midiPlayer.isRunning()) {
			TGChannel channel = ((TGChannel) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL));
			Short bank = ((Short) actionContext.getAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK));
			Short program = ((Short) actionContext.getAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM));
			
			boolean bankChange = (bank != null && bank.shortValue() != channel.getBank());
			boolean programChange = (program != null && program.shortValue() != channel.getProgram());
			if( bankChange || programChange ){
				midiPlayer.updatePrograms();
			}else{
				midiPlayer.updateControllers();
			}
		}
		
		// Call super update.
		super.update(context, actionContext);
	}
}
