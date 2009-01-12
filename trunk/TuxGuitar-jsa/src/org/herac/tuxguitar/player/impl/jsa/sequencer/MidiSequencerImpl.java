package org.herac.tuxguitar.player.impl.jsa.sequencer;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.player.base.MidiTransmitter;

public class MidiSequencerImpl implements MidiSequencer,MidiSequenceLoader{
	
	private static final int TICK_MOVE = 1;
	
	private Sequencer sequencer;
	private Transmitter sequencerTransmitter;
	private MidiTransmitter transmitter;
	
	public MidiSequencerImpl(Sequencer sequencer){
		this.sequencer = sequencer;
	}
	
	public synchronized void open() {
		try {
			if(!this.sequencer.isOpen()){
				this.sequencer.open();
				this.closeTransmitter();
				this.openTransmitter();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public synchronized void close() {
		try {
			if(this.sequencer.isOpen()){
				this.sequencer.close();
				this.closeTransmitter();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void openTransmitter(){
		try {
			this.sequencerTransmitter = getSequencer().getTransmitter();
			this.sequencerTransmitter.setReceiver( new MidiReceiverImpl(this) );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void closeTransmitter(){
		try {
			if(this.sequencerTransmitter != null){
				this.sequencerTransmitter.close();
				this.sequencerTransmitter = null;
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	protected Sequencer getSequencer() {
		this.open();
		return this.sequencer;
	}
	
	public MidiSequenceHandler createSequence(int tracks) {
		this.resetTracks();
		return new MidiSequenceHandlerImpl(this,tracks);
	}
	
	public synchronized MidiTransmitter getTransmitter() {
		return this.transmitter;
	}
	
	public synchronized void setTransmitter(MidiTransmitter transmitter) {
		this.transmitter = transmitter;
	}
	
	public long getTickLength() {
		try {
			return getSequencer().getTickLength();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return 0;
	}
	
	public long getTickPosition() {
		try {
			return (getSequencer().getTickPosition() + TICK_MOVE);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return 0;
	}
	
	public void setTickPosition(long tickPosition) {
		try {
			this.getSequencer().setTickPosition(tickPosition - TICK_MOVE);
			this.reset( false );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public boolean isRunning() {
		try {
			return getSequencer().isRunning();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return false;
	}
	
	public void setMute(int index, boolean mute) {
		try {
			getSequencer().setTrackMute(index, mute);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void setSolo(int index, boolean solo) {
		try {
			getSequencer().setTrackSolo(index, solo);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void setSequence(Sequence sequence){
		try {
			getSequencer().setSequence(sequence);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void start() {
		try {
			getSequencer().start();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			this.getSequencer().stop();
			this.reset( true );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void reset(boolean systemReset){
		try {
			this.getTransmitter().sendAllNotesOff();
			for(int channel = 0; channel < 16;channel ++){
				this.getTransmitter().sendPitchBend(channel, 64);
			}
			if( systemReset ){
				this.getTransmitter().sendSystemReset();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void resetTracks(){
		try {
			Sequence sequence = this.getSequencer().getSequence();
			if(sequence != null){
				Track[] tracks = sequence.getTracks();
				if( tracks != null ){
					int count = tracks.length;
					for( int i = 0 ; i < count; i++ ){
						this.setSolo( i , false );
						this.setMute( i , false );
					}
				}
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public String getKey() {
		return this.sequencer.getDeviceInfo().getName();
	}
	
	public String getName() {
		return this.sequencer.getDeviceInfo().getName();
	}
}
