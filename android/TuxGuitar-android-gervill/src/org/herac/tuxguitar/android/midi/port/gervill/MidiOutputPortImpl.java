package org.herac.tuxguitar.android.midi.port.gervill;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortImpl extends GMOutputPort{

	private TGContext context;
	private MidiSynthesizerManager synthManager;
	private String soundBankResource;
	private String soundBankName;

	public MidiOutputPortImpl(TGContext context, String soundBankResource, String soundBankName) {
		this.context = context;
		this.soundBankName = soundBankName;
		this.soundBankResource = soundBankResource;
	}

	public void open() {
		if( this.synthManager != null ) {
			this.synthManager.close();
		}
		this.synthManager = new MidiSynthesizerManager(this.context, this.soundBankResource);
		this.synthManager.open();
	}
	
	public void close(){
		if( this.synthManager != null ) {
			this.synthManager.close();
		}
	}
	
	public GMReceiver getReceiver() {
		if( this.synthManager != null ) {
			return this.synthManager.getReceiver();
		}
		return null;
	}
	
	public void check() throws MidiPlayerException{
		if( this.synthManager == null || !this.synthManager.isSynthesizerLoaded()) {
			throw new MidiPlayerException("jsa.error.midi.unavailable");
		}
	}

	public String getKey() {
		return ("tuxguitar-gervill." + this.soundBankResource);
	}

	public String getName() {
		return ("Gervill (" + this.soundBankName + ")");
	}
}

