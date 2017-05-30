package org.herac.tuxguitar.midi.synth;

import javax.sound.midi.ShortMessage;

public interface TGMidiProcessor extends TGAudioProcessor {
	
	void queueMidiMessage(ShortMessage message);
}
