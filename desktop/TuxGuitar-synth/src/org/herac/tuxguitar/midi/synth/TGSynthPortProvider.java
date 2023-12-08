package org.herac.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

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
