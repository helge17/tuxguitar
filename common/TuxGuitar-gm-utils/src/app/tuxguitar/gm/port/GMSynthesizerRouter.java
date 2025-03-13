package app.tuxguitar.gm.port;

import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGException;

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
