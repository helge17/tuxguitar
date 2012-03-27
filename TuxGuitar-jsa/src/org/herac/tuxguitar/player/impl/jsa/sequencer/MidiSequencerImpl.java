package org.herac.tuxguitar.player.impl.jsa.sequencer;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.player.base.MidiTransmitter;

public class MidiSequencerImpl implements MidiSequencer,MidiSequenceLoader{
	
	private static final int TICK_MOVE = 1;
	
	private Object lock;
	private Sequencer sequencer;
	private Transmitter sequencerTransmitter;
	private MidiTransmitter transmitter;
	
	public MidiSequencerImpl(Sequencer sequencer){
		this.lock = new Object();
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
	
	protected Sequencer getSequencer(boolean open) {
		if( open ){
			this.open();
		}
		return this.sequencer;
	}
	
	protected Sequencer getSequencer() {
		return this.getSequencer(true);
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
			this.reset();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
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
			this.setRunning( true );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			this.setRunning( false );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public boolean isRunning() {
		try {
			return ( getSequencer( false ) != null && getSequencer( false ).isRunning() );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return false;
	}
	
	public void setRunning( boolean running ) {
		try {
			synchronized ( this.lock ) {
				if( running && !this.isRunning() ){
					this.getSequencer().start();
				}else if( !running && this.isRunning() ){
					this.getSequencer().stop();
					this.reset();
				}
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void reset(){
		try {
			this.getTransmitter().sendAllNotesOff();
			this.getTransmitter().sendPitchBendReset();
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
	
	public void check() throws MidiPlayerException {
		this.getSequencer( true );
		if( this.sequencer == null || !this.sequencer.isOpen() ){
			throw new MidiPlayerException(TuxGuitar.getProperty("jsa.error.midi.unavailable"));
		}
	}
}
