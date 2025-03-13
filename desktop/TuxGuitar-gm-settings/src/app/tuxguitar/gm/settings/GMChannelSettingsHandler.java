package app.tuxguitar.gm.settings;

import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import app.tuxguitar.gm.port.GMOutputPort;
import app.tuxguitar.gm.port.GMSynthesizer;
import app.tuxguitar.player.base.MidiDevice;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;

public class GMChannelSettingsHandler implements TGChannelSettingsHandler{

	private TGContext context;

	public GMChannelSettingsHandler(TGContext context) {
		this.context = context;
	}

	public boolean isMidiDeviceSupported(MidiDevice midiDevice) {
		return (midiDevice instanceof GMOutputPort);
	}

	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) {
		try {
			if( isMidiDeviceSupported(midiDevice) ) {
				GMSynthesizer synthesizer = (GMSynthesizer) ((GMOutputPort) midiDevice).getSynthesizer();
				return new GMChannelSettingsDialog(this.context, channel, song, synthesizer);
			}
		} catch (MidiPlayerException e) {
			throw new TGException(e);
		}
		return null;
	}
}
