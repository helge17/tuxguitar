package org.herac.tuxguitar.player.impl.midiport.alsa;

import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiOutputPortProviderImpl implements MidiOutputPortProvider{
	
	private MidiSystem midiSystem;
	
	public MidiOutputPortProviderImpl(){
		super();
	}
	
	public List<MidiOutputPort> listPorts() {
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
