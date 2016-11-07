package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;

public class GMSynthesizer implements MidiSynthesizer{
	
	private GMOutputPort midiOutputPort;
	private GMChannelRouter gmChannelRouter;
	
	public GMSynthesizer(GMOutputPort midiOutputPort){
		this.midiOutputPort = midiOutputPort;
		this.gmChannelRouter = new GMChannelRouter();
	}
	
	public void closeChannel(MidiChannel midiChannel){
		if( midiChannel instanceof GMChannel ){
			this.gmChannelRouter.removeRoute(((GMChannel) midiChannel).getRoute());
		}
	}
	
	public MidiChannel openChannel(int channelId) throws MidiPlayerException {
		return new GMChannel(channelId, this.gmChannelRouter, this.midiOutputPort.getReceiver());
	}

	public boolean isChannelOpen(MidiChannel midiChannel) throws MidiPlayerException {
		return true;
	}
}
