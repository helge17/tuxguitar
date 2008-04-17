package org.herac.tuxguitar.player.base;

import java.util.List;

public interface MidiPortProvider {
	
	public List listPorts() throws MidiPlayerException;
	
	public void closeAll() throws MidiPlayerException;
	
}
