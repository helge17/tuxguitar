package org.herac.tuxguitar.player.base;

import java.util.List;

public interface MidiOutputPortProvider {
	
	public List<MidiOutputPort> listPorts() throws MidiPlayerException;
	
	public void closeAll() throws MidiPlayerException;
	
}
