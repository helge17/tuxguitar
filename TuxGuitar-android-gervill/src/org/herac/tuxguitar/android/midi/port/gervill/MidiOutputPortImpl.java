package org.herac.tuxguitar.android.midi.port.gervill;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortImpl extends GMOutputPort{
	
	private String key;
	private String name;
	private MidiSynthesizerManager synthManager;
	
	public MidiOutputPortImpl(TGContext context){
		this.synthManager = new MidiSynthesizerManager(context);
		this.key = this.synthManager.getSynth().getDeviceInfo().getName();
		this.name = this.synthManager.getSynth().getDeviceInfo().getName();
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void open(){
		this.synthManager.open();
	}
	
	public void close(){
		this.synthManager.close();
	}
	
	public GMReceiver getReceiver(){
		return this.synthManager.getReceiver();
	}
	
	public void check() throws MidiPlayerException{
		if(!this.synthManager.isSynthesizerLoaded()){
			throw new MidiPlayerException("jsa.error.midi.unavailable");
		}
	}
}

