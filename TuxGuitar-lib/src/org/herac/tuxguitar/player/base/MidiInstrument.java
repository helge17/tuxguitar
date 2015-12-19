package org.herac.tuxguitar.player.base;

import org.herac.tuxguitar.song.models.TGChannelNames;

public class MidiInstrument {
	
	public static final MidiInstrument[] INSTRUMENT_LIST = createDefaultInstruments();
	
	private String name;
	
	public MidiInstrument(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	private static MidiInstrument[] createDefaultInstruments() {
		MidiInstrument[] instruments = new MidiInstrument[TGChannelNames.DEFAULT_NAMES.length];
		for(int i = 0 ; i < TGChannelNames.DEFAULT_NAMES.length; i++) {
			instruments[i] = new MidiInstrument(TGChannelNames.DEFAULT_NAMES[i]);
		}
		return instruments;
	}
}