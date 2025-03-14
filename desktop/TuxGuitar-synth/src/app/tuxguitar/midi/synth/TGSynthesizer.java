package app.tuxguitar.midi.synth;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;

public class TGSynthesizer extends TGSynthModel {

	private TGSynthThread synthesizerThread;
	private TGSynthSettings settings;

	public TGSynthesizer(TGContext context) {
		super(context);

		this.settings = new TGSynthSettings(context);
		this.synthesizerThread = new TGSynthThread(this);
	}

	public boolean isOpen(){
		return this.synthesizerThread.isRunning();
	}

	public void open() throws MidiPlayerException{
		this.settings.loadPrograms(this);
		this.synthesizerThread.start();
	}

	public void close() throws MidiPlayerException{
		if( this.isOpen() ){
			this.synthesizerThread.stop();
			this.closeChannels();
		}
	}

	public TGSynthSettings getSettings() {
		return settings;
	}
}
