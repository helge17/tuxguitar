package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;

public abstract class GMOutputPort implements MidiOutputPort{
	
	private GMSynthesizer midiSynthesizer;
	
	public GMOutputPort(){
		this.midiSynthesizer = new GMSynthesizer(this);
	}
	
	public abstract GMReceiver getReceiver();
	
	public MidiSynthesizer getSynthesizer() throws MidiPlayerException{
		return this.midiSynthesizer;
	}
}
