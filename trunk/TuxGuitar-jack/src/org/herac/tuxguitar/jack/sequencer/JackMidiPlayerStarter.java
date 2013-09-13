package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerListener;

public class JackMidiPlayerStarter implements MidiPlayerListener{
	
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
	
	public void notifyCountDownStarted() {
		this.countDownByPass = (this.jackSequencer.getJackClient().isTransportRunning() && getPlayer().getCountDown().isEnabled());
		if( this.countDownByPass ){
			this.getPlayer().getCountDown().setEnabled( false );
		}
	}
	
	public void notifyCountDownStopped() {
		if( this.countDownByPass ){
			this.countDownByPass = false;
			this.getPlayer().getCountDown().setEnabled( true );
		}
	}
	
	public void notifyStarted() {
		// TODO Auto-generated method stub
	}
	
	public void notifyStopped() {
		// TODO Auto-generated method stub
	}
	
	public void notifyLoop() {
		// TODO Auto-generated method stub
	}
}
