package org.herac.tuxguitar.app.view.dialog.channel;

import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public interface TGChannelSettingsHandler {
	
	boolean isMidiDeviceSupported(MidiDevice midiDevice);
	
	TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) ;
}
