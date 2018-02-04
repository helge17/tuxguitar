package org.herac.tuxguitar.midi.synth;

import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

public class TGSynthesizer extends TGSynthModel {
	
	public static final int BANKS_LENGTH = 129;
	public static final int PROGRAMS_LENGTH = 128;
	
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
