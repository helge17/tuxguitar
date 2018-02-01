package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

public interface VSTCommand<T> {
	
	T process() throws IOException;
}
