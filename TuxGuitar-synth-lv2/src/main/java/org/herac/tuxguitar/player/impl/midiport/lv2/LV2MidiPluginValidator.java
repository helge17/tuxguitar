package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;

public class LV2MidiPluginValidator implements LV2PluginValidator {
	
	private static final String INSTRUMENT_CATEGORY = "Instrument";

	@Override
	public boolean validate(LV2Plugin plugin) {
		if( plugin == null ) {
			return false;
		}
		if( plugin.getName() == null ) {
			System.err.println("Name not found: " + plugin.getUri());
			return false;
		}
		return (INSTRUMENT_CATEGORY.equals(plugin.getCategory()) && plugin.getMidiInputPortCount() > 0 && plugin.getAudioOutputPortCount() > 0);
	}
}
