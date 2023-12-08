package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;

public interface LV2PluginValidator {
	
	boolean validate(LV2Plugin plugin);
}
