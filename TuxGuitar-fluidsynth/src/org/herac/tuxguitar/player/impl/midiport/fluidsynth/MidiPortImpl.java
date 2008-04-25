package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.io.File;

import org.herac.tuxguitar.player.base.MidiOut;
import org.herac.tuxguitar.player.base.MidiPort;
import org.herac.tuxguitar.player.impl.midiport.fluidsynth.MidiOutImpl;

public class MidiPortImpl extends MidiPort{
	
	private MidiSynth synth;
	private MidiOutImpl midiOut;
	private String soundFont;
	
	public MidiPortImpl(MidiSynth synth,File soundfont){
		super(getUniqueKey(soundfont),getUniqueName(soundfont));
		this.soundFont = soundfont.getAbsolutePath();
		this.midiOut = new MidiOutImpl(synth);
		this.synth = synth;
	}
	
	public void open(){
		if(!this.synth.isConnected(this)){
			this.synth.connect(this);
		}
	}
	
	public void close(){
		if(this.synth.isConnected(this)){
			this.synth.disconnect(this);
		}
	}
	
	public MidiOut out(){
		this.open();
		return this.midiOut;
	}
	
	public void check(){
		// Not implemented
	}
	
	public String getSoundFont() {
		return this.soundFont;
	}
	
	public static String getUniqueKey(File soundfont){
		return ("tuxguitar-fluidsynth_" + soundfont.getAbsolutePath());
	}
	
	private static String getUniqueName(File soundfont){
		String name = soundfont.getName();
		int extensionIndex = name.lastIndexOf(".");
		if( extensionIndex > 0 ){
			name = name.substring( 0, extensionIndex );
		}
		return ("TG Fluidsynth " + "[" + name + "]");
	}
}
