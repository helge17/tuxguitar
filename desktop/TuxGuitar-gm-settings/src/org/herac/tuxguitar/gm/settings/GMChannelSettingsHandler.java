package org.herac.tuxguitar.gm.settings;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMSynthesizer;
import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;

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
