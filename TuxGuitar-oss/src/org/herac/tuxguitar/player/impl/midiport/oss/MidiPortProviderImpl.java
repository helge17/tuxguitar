package org.herac.tuxguitar.player.impl.midiport.oss;

import java.util.List;

import org.herac.tuxguitar.player.base.MidiPortProvider;

public class MidiPortProviderImpl implements MidiPortProvider{
	
	private MidiSystem midiSystem;
	
	public MidiPortProviderImpl(){
		super();
	}
	
	public List listPorts() {
		if(this.midiSystem == null){
			this.midiSystem = new MidiSystem();
		}
		if(!this.midiSystem.isOpen()){
			this.midiSystem.open(MidiConfigUtils.getDevice());
		}
		return this.midiSystem.findPorts();
	}
	
	public void closeAll(){
		if(this.midiSystem != null){
			this.midiSystem.close();
			this.midiSystem.finalize();
			this.midiSystem = null;
		}
	}
	
	public void updateDevice(String device){
		if(this.midiSystem != null && this.midiSystem.isOpen()){
			MidiPortImpl connection = this.midiSystem.getConnection();
			if(connection != null){
				connection.close();
			}
			this.midiSystem.close();
			this.midiSystem.open(device);
			if(connection != null){
				connection.open();
			}
		}
	}
}
