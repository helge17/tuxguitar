package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;
import org.herac.tuxguitar.util.TGContext;

public class JackMidiPlayerStarter implements TGEventListener {
	
	private TGContext context;
	private JackSequencer jackSequencer;
	private boolean countDownByPass;
	
	public JackMidiPlayerStarter(TGContext context, JackSequencer jackSequencer){
		this.context = context;
		this.jackSequencer = jackSequencer;
	}
	
	private MidiPlayer getPlayer(){
		return MidiPlayer.getInstance(this.context);
	}
	
	public void open(){
		getPlayer().addListener(this);
	}
	
	public void close(){
		getPlayer().removeListener(this);
	}
	
	public void start(){
		TGTransport.getInstance(this.context).play();
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
			int type = ((Integer)event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			if( type == MidiPlayerEvent.NOTIFY_COUNT_DOWN_STARTED ){
				this.processCountDownStarted();
			} else if( type == MidiPlayerEvent.NOTIFY_COUNT_DOWN_STOPPED ){
				this.processCountDownStopped();
			}
		}
	}
}
