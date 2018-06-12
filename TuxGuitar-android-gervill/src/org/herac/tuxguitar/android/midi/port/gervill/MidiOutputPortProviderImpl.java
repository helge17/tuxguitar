package org.herac.tuxguitar.android.midi.port.gervill;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class MidiOutputPortProviderImpl implements MidiOutputPortProvider{
	
	private TGContext context;
	private List<MidiOutputPort> ports;
	
	public MidiOutputPortProviderImpl(TGContext context){
		this.context = context;
	}
	
	public List<MidiOutputPort> listPorts() {
		if( this.ports == null ) {
			this.ports = new ArrayList<MidiOutputPort>();

			MidiSettings midiSettings = new MidiSettings(context);
			int count = midiSettings.getSoundBankCount();
			for(int i = 0 ; i < count; i++) {
				this.ports.add(new MidiOutputPortImpl(this.context, midiSettings.getSoundBankResource(i), midiSettings.getSoundBankName(i)));
			}
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
