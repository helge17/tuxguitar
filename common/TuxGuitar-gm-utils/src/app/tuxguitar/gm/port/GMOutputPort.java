package app.tuxguitar.gm.port;

import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSynthesizer;

public abstract class GMOutputPort implements MidiOutputPort{

	private GMSynthesizer midiSynthesizer;

	public GMOutputPort(){
		this.midiSynthesizer = new GMSynthesizer(this);
	}

	public abstract GMReceiver getReceiver() throws MidiPlayerException;

	public MidiSynthesizer getSynthesizer() throws MidiPlayerException{
		return this.midiSynthesizer;
	}
}
