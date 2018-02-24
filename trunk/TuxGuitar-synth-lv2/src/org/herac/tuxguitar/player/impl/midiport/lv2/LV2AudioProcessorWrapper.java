package org.herac.tuxguitar.player.impl.midiport.lv2;

import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;

public class LV2AudioProcessorWrapper implements TGAudioProcessor {
	
	public static final String PARAM_LV2_URI = "lv2.uri";
	
	private LV2World world;
	private LV2AudioProcessor target;
	
	public LV2AudioProcessorWrapper(LV2World world) {
		this.world = world;
	}
	
	public LV2World getWorld() {
		return this.world;	
	}
	
	public LV2AudioProcessor getTarget() {
		return this.target;
	}
	
	public void finalize() {
		if( this.target != null ) {
			this.target.finalize();
		}
	}
	
	public void open(String uri) {
		try {
			if( this.target == null ) {
				List<LV2Plugin> plugins = this.world.getPlugins();
				for(LV2Plugin plugin : plugins) {
					if( plugin.getUri().equals(uri)) {
						this.target = new LV2AudioProcessor(plugin);
					}
				}
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
		} else if(parameters.containsKey(PARAM_LV2_URI)) {
			this.open(parameters.get(PARAM_LV2_URI));
		}
	}
	
	public void storeParameters(Map<String, String> parameters) {
		if( this.target != null ) {
			this.target.storeParameters(parameters);
			
			parameters.put(PARAM_LV2_URI, this.target.getPlugin().getUri());
		}
	}
	
	public boolean isBusy() {
		return false;
	}
}
