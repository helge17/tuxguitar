package app.tuxguitar.player.impl.midiport.lv2;

import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import app.tuxguitar.util.TGContext;

public class LV2MidiProcessorFactory implements TGMidiProcessorFactory {

	private TGContext context;
	private LV2World world;

	public LV2MidiProcessorFactory(TGContext context, LV2World world) {
		this.context = context;
		this.world = world;
	}

	public String getType() {
		return LV2Module.MIDI_TYPE;
	}

	public TGMidiProcessor createProcessor() {
		try {
			return new LV2MidiProcessor(this.context, this.world);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
