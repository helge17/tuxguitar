package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.player.base.MidiDevice;

public class GMChannelSettingsHandler implements TGChannelSettingsHandler{
	
	public TGChannelSettingsDialog getChannelSettingsDialog() {
		return new GMChannelSettingsDialog();
	}
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice) {
		return (midiDevice instanceof GMOutputPort);
	}
}
