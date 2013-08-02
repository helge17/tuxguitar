package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class GMChannel implements MidiChannel{
	
	public static final short PERCUSSION_BANK = 128;
	
	private GMReceiver receiver;
	private GMChannelRoute route;
	private GMChannelRouter router;
	
	public GMChannel(int channelId, GMChannelRouter router, GMReceiver receiver){
		this.receiver = receiver;
		this.route = new GMChannelRoute(channelId);
		this.router = router;
		this.router.configureRoutes(this.route, false);
	}
	
	public GMChannelRoute getRoute(){
		return this.route;
	}
	
	public void sendAllNotesOff() throws MidiPlayerException {
		this.receiver.sendAllNotesOff();
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		this.receiver.sendNoteOn(resolveChannel(bendMode), key, velocity);
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		this.receiver.sendNoteOff(resolveChannel(bendMode), key, velocity);
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException {
		this.receiver.sendPitchBend(resolveChannel(bendMode), value);
	}
	
	public void sendProgramChange(int value) throws MidiPlayerException {
		this.receiver.sendProgramChange(this.route.getChannel1(), value);
		if( this.route.getChannel1() != this.route.getChannel2() ){
			this.receiver.sendProgramChange(this.route.getChannel2(), value);
		}
	}

	public void sendControlChange(int controller, int value) throws MidiPlayerException {
		if( controller == MidiControllers.BANK_SELECT && value == PERCUSSION_BANK ){
			this.router.configureRoutes(this.route, true);
		}
		this.receiver.sendControlChange(this.route.getChannel1(), controller, value);
		if( this.route.getChannel1() != this.route.getChannel2() ){
			this.receiver.sendControlChange(this.route.getChannel2(), controller, value);
		}
	}
	
	public void sendParameter(String key, String value) throws MidiPlayerException{
		if( key.equals(GMChannelRoute.PARAMETER_GM_CHANNEL_1) ){
			this.route.setChannel1(Integer.parseInt(value));
			this.router.configureRoutes(this.route, false);
		}
		if( key.equals(GMChannelRoute.PARAMETER_GM_CHANNEL_2) ){
			this.route.setChannel2(Integer.parseInt(value));
			this.router.configureRoutes(this.route, false);
		}
	}
	
	private int resolveChannel(boolean bendMode){
		return (bendMode ? this.route.getChannel2() : this.route.getChannel1());
	}
}
