package org.herac.tuxguitar.android.midimaster.port;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayerException;

import java.util.ArrayList;
import java.util.List;

public class MidiOutputPortProviderImpl implements MidiOutputPortProvider{

	private List<MidiOutputPort> ports;
	
	public MidiOutputPortProviderImpl(){
		super();
	}

	public List<MidiOutputPort> listPorts() {
		if( this.ports == null ) {
			this.ports = new ArrayList<MidiOutputPort>();
			this.ports.add( new MidiOutputPortImpl() );
		}
		return this.ports;
	}
	
	public void closeAll() throws MidiPlayerException{
		if( this.ports != null ) {
			for(int i = 0 ; i < this.ports.size() ; i++) {
				this.ports.get(i).close();
			}
			this.ports.clear();
		}
	}
}
