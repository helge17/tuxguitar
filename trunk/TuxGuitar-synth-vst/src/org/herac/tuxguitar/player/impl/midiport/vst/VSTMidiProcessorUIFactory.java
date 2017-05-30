package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import org.herac.tuxguitar.player.impl.midiport.vst.ui.VSTAudioProcessorUI;
import org.herac.tuxguitar.util.TGContext;

public class VSTMidiProcessorUIFactory implements TGAudioProcessorUIFactory {
	
	private TGContext context;
	
	public VSTMidiProcessorUIFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType() {
		return VSTType.VSTI.toString();
	}
	
	public TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback) {
		return new VSTAudioProcessorUI(this.context, (VSTAudioProcessor) processor, VSTType.VSTI, callback);
	}
}
