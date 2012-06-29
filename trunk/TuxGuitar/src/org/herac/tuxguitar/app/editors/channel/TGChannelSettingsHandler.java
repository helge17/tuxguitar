package org.herac.tuxguitar.app.editors.channel;

import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;

public interface TGChannelSettingsHandler {
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice);
	
	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel) ;
}
