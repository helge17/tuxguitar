package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import java.util.List;

import org.herac.tuxguitar.player.base.MidiPortProvider;
import org.herac.tuxguitar.player.impl.midiport.coreaudio.MidiOutImpl;

public class MidiPortReaderCoreAudio implements MidiPortProvider{

	private static final MidiOutImpl midiOut = new MidiOutImpl();
	
	public MidiPortReaderCoreAudio(){
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
