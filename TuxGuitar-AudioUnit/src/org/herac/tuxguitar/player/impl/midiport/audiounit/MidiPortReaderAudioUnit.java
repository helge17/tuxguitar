package org.herac.tuxguitar.player.impl.midiport.audiounit;

import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.impl.midiport.audiounit.MidiReceiverImpl;

public class MidiPortReaderAudioUnit implements MidiOutputPortProvider{

	private static final MidiReceiverImpl midiOut = new MidiReceiverImpl();
	
	public MidiPortReaderAudioUnit(){
		super();
	}
	
	public List listPorts() {
		if(!midiOut.isOpen()){
			midiOut.open();
		}
		return midiOut.listPorts();
	}

	public void closeAll(){	
		midiOut.close();
	}
	
}
