package org.herac.tuxguitar.player.impl.midiport.winmm;

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
		return this.midiSystem.findPorts();
	}

	public void closeAll(){	
		if(this.midiSystem != null){
			this.midiSystem.finalize();
			this.midiSystem = null;
		}
	}
	
}
