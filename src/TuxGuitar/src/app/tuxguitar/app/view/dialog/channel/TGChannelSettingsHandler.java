package app.tuxguitar.app.view.dialog.channel;

import app.tuxguitar.player.base.MidiDevice;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;

public interface TGChannelSettingsHandler {

	boolean isMidiDeviceSupported(MidiDevice midiDevice);

	TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) ;
}
