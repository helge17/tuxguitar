package app.tuxguitar.player.impl.midiport.audiounit;

import java.util.List;

import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.util.TGContext;

public class MidiPortReaderAudioUnit implements MidiOutputPortProvider{

	private final MidiReceiverImpl midiOut;

	public MidiPortReaderAudioUnit(TGContext context) {
		super();
		midiOut = new MidiReceiverImpl(context);
	}

	public boolean setSoundbankPath(String soundbankPath) {
		return midiOut.changeSoundBank(soundbankPath) == 0;
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
