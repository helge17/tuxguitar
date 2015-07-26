package org.herac.tuxguitar.app.view.dialog.channel;

import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public interface TGChannelSettingsHandler {
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice);
	
	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) ;
}
