package app.tuxguitar.player.impl.midiport.vst;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import app.tuxguitar.util.TGContext;

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
