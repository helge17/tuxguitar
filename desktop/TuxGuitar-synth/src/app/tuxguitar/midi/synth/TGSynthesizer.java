package app.tuxguitar.midi.synth;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;

public class TGSynthesizer extends TGSynthModel {

	private TGSynthThread synthesizerThread;
	private TGSynthSettings settings;
	// what it plays through while open: an output an app registered (the live audio device), else its Java Sound thread
	private TGSynthOutput output;

	public TGSynthesizer(TGContext context) {
		super(context);

		this.settings = new TGSynthSettings(context);
		this.synthesizerThread = new TGSynthThread(this);
	}

	public boolean isOpen(){
		return this.output != null;
	}

	public void open() throws MidiPlayerException{
		this.settings.loadPrograms(this);
		this.output = TGSynthOutput.open(this.getContext(), this, this.synthesizerThread);
	}

	public void close() throws MidiPlayerException{
		if( this.isOpen() ){
			this.output.stop(this);
			this.output = null;
			this.closeChannels();
		}
	}

	public TGSynthSettings getSettings() {
		return settings;
	}
}
