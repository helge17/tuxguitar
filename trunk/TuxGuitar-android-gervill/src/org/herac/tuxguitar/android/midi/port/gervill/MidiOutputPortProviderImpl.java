package org.herac.tuxguitar.android.midi.port.gervill;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderImpl implements MidiOutputPortProvider{
	
	private TGContext context;
	private List<MidiOutputPort> ports;
	
	public MidiOutputPortProviderImpl(TGContext context){
		this.context = context;
	}
	
	public List<MidiOutputPort> listPorts() {
		if( this.ports == null ) {
			this.ports = new ArrayList<MidiOutputPort>();
			this.ports.add(new MidiOutputPortImpl(this.context));
		}
		return this.ports;
	}
	
	public void closeAll() throws MidiPlayerException{
		if( this.ports != null ) {
			for(int i = 0 ; i < this.ports.size() ; i++) {
				((MidiOutputPort)this.ports.get(i)).close();
			}
			this.ports.clear();
		}
	}
}
