package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;

public class JackMidiPlayerStarter implements TGEventListener {
	
	private JackSequencer jackSequencer;
	private boolean countDownByPass;
	
	public JackMidiPlayerStarter(JackSequencer jackSequencer){
		this.jackSequencer = jackSequencer;
	}
	
	private MidiPlayer getPlayer(){
		return MidiPlayer.getInstance();
	}
	
	public void open(){
		getPlayer().addListener(this);
	}
	
	public void close(){
		getPlayer().removeListener(this);
	}
	
	public void start(){
		TuxGuitar.instance().getTransport().play();
	}
	
	public void processCountDownStarted() {
		this.countDownByPass = (this.jackSequencer.getJackClient().isTransportRunning() && getPlayer().getCountDown().isEnabled());
		if( this.countDownByPass ){
			this.getPlayer().getCountDown().setEnabled( false );
		}
	}
	
	public void processCountDownStopped() {
		if( this.countDownByPass ){
			this.countDownByPass = false;
			this.getPlayer().getCountDown().setEnabled( true );
		}
	}
	
	public void processEvent(TGEvent event) {
		if( MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getProperty(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			if( type == MidiPlayerEvent.NOTIFY_COUNT_DOWN_STARTED ){
				this.processCountDownStarted();
			} else if( type == MidiPlayerEvent.NOTIFY_COUNT_DOWN_STOPPED ){
				this.processCountDownStopped();
			}
		}
	}
}
