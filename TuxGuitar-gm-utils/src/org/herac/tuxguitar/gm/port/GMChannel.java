package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class GMChannel implements MidiChannel{
	
	public static final short PERCUSSION_BANK = 128;
	
	private boolean percussionChannel;
	private GMReceiver receiver;
	private GMChannelRoute route;
	private GMChannelRouter router;
	private GMProgram program;
	
	public GMChannel(int channelId, GMChannelRouter router, GMReceiver receiver){
		this.receiver = receiver;
		this.router = router;
		this.route = new GMChannelRoute(channelId);
		this.program = new GMProgram();
		this.percussionChannel = false;
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
		this.program.setProgram(value);
		this.router.configureRoutes(this.route, this.percussionChannel);
		this.receiver.sendProgramChange(this.route.getChannel1(), value);
		if( this.route.getChannel1() != this.route.getChannel2() ){
			this.receiver.sendProgramChange(this.route.getChannel2(), value);
		}
	}

	public void sendControlChange(int controller, int value) throws MidiPlayerException {
		this.program.setController(controller, value);
		if( controller == MidiControllers.BANK_SELECT ) {
			this.percussionChannel = (value == PERCUSSION_BANK);
			this.router.configureRoutes(this.route, this.percussionChannel);
		}
		this.receiver.sendControlChange(this.route.getChannel1(), controller, value);
		if( this.route.getChannel1() != this.route.getChannel2() ){
			this.receiver.sendControlChange(this.route.getChannel2(), controller, value);
		}
	}
	
	public void sendParameter(String key, String value) throws MidiPlayerException{
		if( key.equals(GMChannelRoute.PARAMETER_GM_CHANNEL_1)) {
			this.program.setChannel1(Integer.parseInt(value));
			this.route.setChannel1(this.program.getChannel1());
			this.router.configureRoutes(this.route, this.percussionChannel);
		}
		if( key.equals(GMChannelRoute.PARAMETER_GM_CHANNEL_2)) {
			this.program.setChannel2(Integer.parseInt(value));
			this.route.setChannel2(this.program.getChannel2());
			this.router.configureRoutes(this.route, this.percussionChannel);
		}
	}
	
	public void sendProgramUpdated() throws MidiPlayerException {
		if(!this.program.isSameChannel(this.route.getChannel1(), this.route.getChannel2())) {
			this.program.setChannel1(this.route.getChannel1());
			this.program.setChannel2(this.route.getChannel2());
			
			if( this.program.getProgram() != null ) {
				this.sendProgramChange(this.program.getProgram());
			}
			for(int controller = 0; controller < this.program.getControllers().length ; controller ++) {
				Integer value = this.program.getController(controller);
				if( value != null ) {
					this.sendControlChange(controller, value);
				}
			}
		}
	}
	
	private int resolveChannel(boolean bendMode){
		return (bendMode ? this.route.getChannel2() : this.route.getChannel1());
	}
}
