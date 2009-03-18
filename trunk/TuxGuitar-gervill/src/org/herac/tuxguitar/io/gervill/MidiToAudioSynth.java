package org.herac.tuxguitar.io.gervill;

import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import com.sun.media.sound.AudioSynthesizer;
import com.sun.media.sound.SoftSynthesizer;

public class MidiToAudioSynth {
	
	public static final AudioFormat	SRC_FORMAT = MidiToAudioSettings.DEFAULT_FORMAT;
	
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
	
	public void openSynth(){
		try {
			if( this.synthesizer == null || !this.synthesizer.isOpen() ){
				this.synthesizer = new SoftSynthesizer();
				this.receiver = this.synthesizer.getReceiver();
				this.stream = ((AudioSynthesizer)this.synthesizer).openStream(SRC_FORMAT, null);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void closeSynth(){
		try {
			if( this.receiver != null ){
				this.receiver.close();
			}
			if( this.synthesizer != null && this.synthesizer.isOpen() ){
				this.synthesizer.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		this.stream = null;
		this.receiver = null;
		this.synthesizer = null;
	}
}
