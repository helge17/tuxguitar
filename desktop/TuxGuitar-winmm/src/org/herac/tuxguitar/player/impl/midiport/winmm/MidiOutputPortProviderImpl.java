package org.herac.tuxguitar.player.impl.midiport.winmm;

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
		return this.midiSystem.findPorts();
	}
	
	public void closeAll(){
		if(this.midiSystem != null){
			this.midiSystem.finalize();
			this.midiSystem = null;
		}
	}
}
