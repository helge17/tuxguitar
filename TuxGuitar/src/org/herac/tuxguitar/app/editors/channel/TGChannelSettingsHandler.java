package org.herac.tuxguitar.app.editors.channel;

import org.herac.tuxguitar.player.base.MidiDevice;

public interface TGChannelSettingsHandler {
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice);
	
	public TGChannelSettingsDialog getChannelSettingsDialog();
}
