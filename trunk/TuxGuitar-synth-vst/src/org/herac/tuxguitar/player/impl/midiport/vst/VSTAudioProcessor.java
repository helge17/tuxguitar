package org.herac.tuxguitar.player.impl.midiport.vst;

import java.io.File;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.player.impl.midiport.vst.jni.VSTPlugin;

public class VSTAudioProcessor implements TGAudioProcessor {
	
	public static final String PARAM_FILE_NAME = "vst.filename";
	
	private VSTEffectProcessor target;
	
	public VSTAudioProcessor() {
		super();
	}
	
	public VSTEffectProcessor getTarget() {
		return this.target;
	}
	
	public void finalize() {
		if( this.target != null ) {
			this.target.finalize();
		}
	}
	
	public void open(String filename) {
		try {
			if( this.target == null ) {
				this.target = new VSTEffectProcessor(new VSTPlugin(new File(filename)));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		if( this.target != null ) {
			this.target.close();
		}
	}

	public boolean isOpen() {
		if( this.target != null ) {
			return this.target.isOpen();
		}
		return false;
	}

	public void fillBuffer(TGAudioBuffer buffer) {
		if( this.target != null ) {
			this.target.fillBuffer(buffer);
		}
	}

	public void queueMidiMessage(byte[] midiMessage) {
		if( this.target != null ) {
			this.target.queueMidiMessage(midiMessage);
		}
	}
	
	public void restoreParameters(Map<String, String> parameters) {
		if( this.target != null ) {
			this.target.restoreParameters(parameters);
		} else if(parameters.containsKey(PARAM_FILE_NAME)) {
			this.open(parameters.get(PARAM_FILE_NAME));
		}
	}
	
	public void storeParameters(Map<String, String> parameters) {
		if( this.target != null ) {
			this.target.storeParameters(parameters);
			
			parameters.put(PARAM_FILE_NAME, this.target.getPlugin().getFile().getAbsolutePath());
		}
	}
}
