package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

public interface LV2Command<T> {
	
	T process() throws IOException;
}
