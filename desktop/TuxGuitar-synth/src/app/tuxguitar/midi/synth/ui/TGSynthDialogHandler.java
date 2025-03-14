package app.tuxguitar.midi.synth.ui;

import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import app.tuxguitar.midi.synth.TGSynthPort;
import app.tuxguitar.midi.synth.TGSynthesizer;
import app.tuxguitar.player.base.MidiDevice;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

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
