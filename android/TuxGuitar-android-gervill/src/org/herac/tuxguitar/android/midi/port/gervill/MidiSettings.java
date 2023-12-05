package org.herac.tuxguitar.android.midi.port.gervill;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class MidiSettings {

	private static final String MIDI_SOUNDBANK_PREFIX = "tuxguitar-gervill.soundbank.";
	
	private TGContext context;
	private TGConfigManager config;
	
	public MidiSettings(TGContext context){
		this.context = context;
	}
	
	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-gervill");
		}
		return this.config;
	}
	
	public void save(){
		this.getConfig().save();
	}
	
	public int getSoundBankCount() {
		return this.getConfig().getIntegerValue(MIDI_SOUNDBANK_PREFIX + "count", 0);
	}
	
	public String getSoundBankName(int index) {
		return this.getConfig().getStringValue(MIDI_SOUNDBANK_PREFIX + index + ".name");
	}

	public String getSoundBankResource(int index) {
		return this.getConfig().getStringValue(MIDI_SOUNDBANK_PREFIX + index + ".resource");
	}
}
