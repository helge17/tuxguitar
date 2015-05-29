package org.herac.tuxguitar.player.impl.midiport.oss;

import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderImpl implements MidiOutputPortProvider{
	
	private TGContext context;
	private MidiSystem midiSystem;
	
	public MidiOutputPortProviderImpl(TGContext context){
		this.context = context;
	}
	
	public List<MidiOutputPort> listPorts() {
		if(this.midiSystem == null){
			this.midiSystem = new MidiSystem();
		}
		if(!this.midiSystem.isOpen()){
			this.midiSystem.open(MidiConfigUtils.getDevice(this.context));
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
			MidiOutputPortImpl connection = this.midiSystem.getConnection();
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
