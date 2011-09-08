package org.herac.tuxguitar.io.gervill;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class MidiToAudioSynth {
	
	private static final AudioFormat SRC_FORMAT = MidiToAudioSettings.DEFAULT_FORMAT;
	private static final String SYNTHESIZER_CLASSNAME = "com.sun.media.sound.SoftSynthesizer";
	private static final String SYNTHESIZER_LOAD_DEFAULT_SOUNDBANK_PARAM = "load default soundbank";
	
	private static MidiToAudioSynth instance;
	
	private Synthesizer synthesizer;
	private AudioInputStream stream;
	private Receiver receiver;
	
	private MidiToAudioSynth(){
		this.stream = null;
		this.receiver = null;
		this.synthesizer = null;
	}
	
	public static MidiToAudioSynth instance(){
		if( instance == null ){
			instance = new MidiToAudioSynth();
		}
		return instance;
	}
	
	public Receiver getReceiver(){
		return this.receiver;
	}
	
	public AudioInputStream getStream(){
		return this.stream;
	}
	
	public void openSynth() throws Throwable {
		if( this.synthesizer == null || !this.synthesizer.isOpen() ){
			this.synthesizer = new com.sun.media.sound.SoftSynthesizer();
			this.receiver = this.synthesizer.getReceiver();
			this.stream = ((com.sun.media.sound.AudioSynthesizer)this.synthesizer).openStream(SRC_FORMAT, getDefaultInfo());
		}
	}
	
	public void closeSynth() throws Throwable {
		if( this.receiver != null ){
			this.receiver.close();
		}
		if( this.synthesizer != null && this.synthesizer.isOpen() ){
			this.synthesizer.close();
		}
		this.stream = null;
		this.receiver = null;
		this.synthesizer = null;
	}
	
	public boolean isAvailable(){
		try {
			Class.forName(SYNTHESIZER_CLASSNAME, false, getClass().getClassLoader() );
			return true;
		} catch (Throwable throwable) {
			return false;
		}
	}
	
	private Map getDefaultInfo(){
		Map map = new HashMap();
		map.put(SYNTHESIZER_LOAD_DEFAULT_SOUNDBANK_PARAM, new Boolean(false));
		return map;
	}
	
	public void loadSoundbank(List patchList, String soundbankPath) throws Throwable {
		Soundbank soundbank = null;
		if( soundbankPath == null || soundbankPath.length() == 0 ){
			soundbank = this.synthesizer.getDefaultSoundbank();
		} else{
			soundbank = MidiSystem.getSoundbank(new File(soundbankPath));
		}
		
		Iterator it = patchList.iterator();
		while( it.hasNext() ){
			Patch patch = (Patch)it.next();
			
			boolean percussion = (patch.getBank() == 128);
			int bank = (percussion ? 0 : patch.getBank());
			int program = patch.getProgram();
			
			Instrument instrument = soundbank.getInstrument(new com.sun.media.sound.ModelPatch(bank, program, percussion));
			if( instrument != null ){
				this.synthesizer.loadInstrument(instrument);
			}
		}
	}
}
