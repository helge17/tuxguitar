package org.herac.tuxguitar.gm.settings;

import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public class GMChannelSettingsHandler implements TGChannelSettingsHandler{
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice) {
		return (midiDevice instanceof GMOutputPort);
	}
	
	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) {
		if( isMidiDeviceSupported(midiDevice) ){
			return new GMChannelSettingsDialog(channel, song);
		}
		return null;
	}
}
