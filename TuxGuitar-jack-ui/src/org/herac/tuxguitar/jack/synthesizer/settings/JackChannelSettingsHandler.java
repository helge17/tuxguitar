package org.herac.tuxguitar.jack.synthesizer.settings;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.jack.synthesizer.JackSynthesizerPort;
import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class JackChannelSettingsHandler implements TGChannelSettingsHandler{
	
	private TGContext context;
	
	public JackChannelSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice) {
		return (midiDevice instanceof JackSynthesizerPort);
	}
	
	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) {
		if( isMidiDeviceSupported(midiDevice) ){
			return new JackChannelSettingsDialog(this.context, channel, song);
		}
		return null;
	}
}
