package org.herac.tuxguitar.gm.port;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;

public class GMSynthesizer implements MidiSynthesizer{
	
	private List<GMChannel> channels;
	private GMOutputPort outputPort;
	private GMChannelRouter router;
	
	public GMSynthesizer(GMOutputPort midiOutputPort){
		this.outputPort = midiOutputPort;
		this.channels = new ArrayList<GMChannel>();
		this.router = new GMSynthesizerRouter(this);
	}
	
	public void closeChannel(MidiChannel midiChannel) throws MidiPlayerException {
		if( this.isChannelOpen(midiChannel) ){
			this.router.removeRoute(((GMChannel) midiChannel).getRoute());
			this.channels.remove(midiChannel);
		}
	}
	
	public MidiChannel openChannel(int channelId) throws MidiPlayerException {
		MidiChannel channel = getChannel(channelId);
		if( channel == null ) {
			channel = new GMChannel(channelId, this.router, this.outputPort.getReceiver());
			
			this.channels.add((GMChannel) channel);
		}
		return channel;
	}

	public MidiChannel getChannel(int channelId) throws MidiPlayerException {
		for(GMChannel channel : this.channels) {
			if( channel.getRoute().getChannelId() == channelId ) {
				return channel;
			}
		}
		return null;
	}
	
	public boolean isChannelOpen(MidiChannel midiChannel) throws MidiPlayerException {
		if( midiChannel instanceof GMChannel ){
			return (this.getChannel(((GMChannel) midiChannel).getRoute().getChannelId()) != null);
		}
		return false;
	}
	
	public boolean isBusy() {
		return false;
	}
	
	public List<GMChannel> getChannels() {
		return this.channels;
	}
	
	public GMChannelRouter getRouter() {
		return this.router;
	}
}
