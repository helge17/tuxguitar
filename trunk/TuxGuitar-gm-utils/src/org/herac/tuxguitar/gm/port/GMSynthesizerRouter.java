package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGException;

public class GMSynthesizerRouter extends GMChannelRouter {
	
	private GMSynthesizer synth;
	
	public GMSynthesizerRouter(GMSynthesizer synth){
		this.synth = synth;
	}
	
	public void configureRoutes(GMChannelRoute route, boolean percussionChannel) {
		try {
			super.configureRoutes(route, percussionChannel);
			
			for(GMChannel gmChannel : this.synth.getChannels()) {
				gmChannel.sendProgramUpdated();
			}
		} catch (MidiPlayerException e) {
			throw new TGException(e);
		}
	}
}
