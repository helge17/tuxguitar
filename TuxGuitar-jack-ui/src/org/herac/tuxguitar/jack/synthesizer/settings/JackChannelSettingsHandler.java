package org.herac.tuxguitar.jack.synthesizer.settings;

import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.jack.synthesizer.JackSynthesizerPort;
import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public class JackChannelSettingsHandler implements TGChannelSettingsHandler{
	
	public JackChannelSettingsHandler() {
		super();
	}
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice) {
		return (midiDevice instanceof JackSynthesizerPort);
	}
	
	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) {
		if( isMidiDeviceSupported(midiDevice) ){
			return new JackChannelSettingsDialog(channel, song);
		}
		return null;
	}
}
