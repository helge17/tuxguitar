package org.herac.tuxguitar.player.impl.midiport.alsa;

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
			this.midiSystem.open();
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
}
