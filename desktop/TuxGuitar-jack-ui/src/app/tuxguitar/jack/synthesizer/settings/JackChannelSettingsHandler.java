package app.tuxguitar.jack.synthesizer.settings;

import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import app.tuxguitar.jack.synthesizer.JackSynthesizerPort;
import app.tuxguitar.player.base.MidiDevice;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
