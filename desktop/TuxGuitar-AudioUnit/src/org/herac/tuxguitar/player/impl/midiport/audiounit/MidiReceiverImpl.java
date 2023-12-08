package org.herac.tuxguitar.player.impl.midiport.audiounit;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiOutputPort;

public class MidiReceiverImpl extends MidiReceiverJNI implements GMReceiver{
	private boolean open; // unncessary
    private boolean connected;	
	private List<MidiOutputPort> ports;
	
	public MidiReceiverImpl(){
		this.ports = new ArrayList<MidiOutputPort>();	
        this.connected = false;
	}

	public void open(){
		super.open();
		this.open = true;
	}

	public void close(){
		if(this.isOpen()){
			this.disconnect();
			super.close();
			this.open = false;
		}
	}	
		
	public boolean isOpen(){
		return (this.open);
	}	
			
	public boolean isConnected(){
		return (this.isOpen() && this.connected);
	}	
			
    public void connect(){
        if(isOpen()){
            if(!isConnected()){             
                this.connected = true;
                this.openDevice();
            }
        }
    }

	public void disconnect() {
		if(isConnected()){
			this.closeDevice();
			this.connected = false;
		}
	}		
		
	public List<MidiOutputPort> listPorts(){
		if(isOpen()){
			this.ports.clear();
			this.ports.add(new MidiPortImpl(this, "AudioUnit graph midi playback" , "audiounit" ));
			return this.ports;
		}
		return new ArrayList<MidiOutputPort>();
	}		

	public void sendSystemReset() {
		if(isOpen()){
			//not implemented
		}
	}
	
	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);		
		}
	}

	public void sendControlChange(int channel, int controller, int value) {
		if(isOpen()){
			super.controlChange(channel, controller, value);
		}
	}

	public void sendNoteOff(int channel, int key, int velocity) {
		if(isOpen()){
			super.noteOff(channel, key, velocity);
		}
	}

	public void sendNoteOn(int channel, int key, int velocity) {
		if(isOpen()){
			super.noteOn(channel, key, velocity);
		}
	}

	public void sendPitchBend(int channel, int value) {
		if(isOpen()){
			super.pitchBend(channel, value);
		}
	}

	public void sendProgramChange(int channel, int value) {
		if(isOpen()){
			super.programChange(channel, value);
		}
	}
}
