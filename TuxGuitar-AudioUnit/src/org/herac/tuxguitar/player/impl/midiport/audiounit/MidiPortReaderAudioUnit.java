package org.herac.tuxguitar.player.impl.midiport.audiounit;

import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiPortReaderAudioUnit implements MidiOutputPortProvider{

	private static final MidiReceiverImpl midiOut = new MidiReceiverImpl();
	
	public MidiPortReaderAudioUnit(){
		super();
	}
	
	public List<MidiOutputPort> listPorts() {
		if(!midiOut.isOpen()){
			midiOut.open();
		}
		return midiOut.listPorts();
	}

	public void closeAll(){	
		midiOut.close();
	}
	
}
