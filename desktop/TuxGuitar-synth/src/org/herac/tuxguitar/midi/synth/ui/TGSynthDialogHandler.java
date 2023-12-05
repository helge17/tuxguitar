package org.herac.tuxguitar.midi.synth.ui;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.midi.synth.TGSynthPort;
import org.herac.tuxguitar.midi.synth.TGSynthesizer;
import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGSynthDialogHandler implements TGChannelSettingsHandler{
	
	private TGContext context;
	
	public TGSynthDialogHandler(TGContext context) {
		this.context = context;
	}
	
	public boolean isMidiDeviceSupported(MidiDevice midiDevice) {
		return (midiDevice instanceof TGSynthPort);
	}
	
	public TGChannelSettingsDialog createChannelSettingsDialog(MidiDevice midiDevice, TGChannel channel, TGSong song) {
		try{
			if( isMidiDeviceSupported(midiDevice) ){
				return new TGSynthDialog(this.context, (TGSynthesizer)((TGSynthPort) midiDevice).getSynthesizer(), channel, song);
			}
		}catch(Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
		return null;
	}
}
