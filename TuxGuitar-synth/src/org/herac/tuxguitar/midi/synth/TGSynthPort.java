package org.herac.tuxguitar.midi.synth;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;
import org.herac.tuxguitar.util.TGContext;

public class TGSynthPort implements MidiOutputPort{	  
	
	private TGContext context;
	private String key;
	private String name;
	private TGSynthesizer synthesizer;
	
	public TGSynthPort(TGContext context, String key, String name) {
		this.context = context;
		this.key = key;
		this.name = name;
		this.synthesizer = new TGSynthesizer(this.context);
	}
	
	public void open() throws MidiPlayerException {
		try {
			this.synthesizer.open();
			
			new TGSynthSettings(this.context).loadPrograms(this.synthesizer);
		} catch ( Throwable throwable ){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}
	
	public void close() throws MidiPlayerException {
		try {
			this.synthesizer.close();
		} catch ( Throwable throwable ){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}
	
	public MidiSynthesizer getSynthesizer() throws MidiPlayerException {
		return this.synthesizer;
	}
	
	public void check() throws MidiPlayerException {
		// Not implemented
	}
	
	public String getKey() {
		return this.key;
	}

	public String getName() {
		return this.name;
	}
}
