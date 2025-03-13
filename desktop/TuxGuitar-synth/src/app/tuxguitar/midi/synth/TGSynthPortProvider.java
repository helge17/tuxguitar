package app.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;

public class TGSynthPortProvider implements MidiOutputPortProvider{

	private TGSynthPort port;

	public TGSynthPortProvider(TGContext context){
		this.port = new TGSynthPort(context, "tuxguitar-synth.port","TuxGuitar Synthesizer");
	}

	public void closeAll() throws MidiPlayerException {
		this.port.close();
	}

	public List<MidiOutputPort> listPorts() throws MidiPlayerException {
		List<MidiOutputPort> ports = new ArrayList<MidiOutputPort>();
		ports.add( this.port );
		return ports;
	}

}
