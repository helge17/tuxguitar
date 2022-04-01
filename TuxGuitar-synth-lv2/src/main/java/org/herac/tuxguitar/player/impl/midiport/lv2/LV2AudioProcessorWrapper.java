package org.herac.tuxguitar.player.impl.midiport.lv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import org.herac.tuxguitar.util.TGContext;

public class LV2AudioProcessorWrapper implements TGAudioProcessor {
	
	public static final String PARAM_LV2_URI = "lv2.uri";
	public static final String PARAM_LV2_PARAM_PREFIX = "lv2.param.";
	
	private TGContext context;
	private LV2World world;
	private LV2AudioProcessor target;
	private Map<String, String> appliedParameters;
	
	public LV2AudioProcessorWrapper(TGContext context, LV2World world) {
		this.context = context;
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
			if( this.target == null || !this.target.isOpen() ) {
				this.appliedParameters = null;
				
				List<LV2Plugin> plugins = this.world.getPlugins();
				for(LV2Plugin plugin : plugins) {
					if( plugin.getUri().equals(uri)) {
						this.target = new LV2AudioProcessor(this.context, plugin);
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
		if(!this.isOpen() && parameters.containsKey(PARAM_LV2_URI)) {
			this.open(parameters.get(PARAM_LV2_URI));
		}
		if( this.isOpen() ) {
			if( this.appliedParameters == null || !this.appliedParameters.equals(parameters)) {
				this.appliedParameters = new HashMap<String, String>(parameters);
				
				List<Integer> portIndices = this.target.getPlugin().getControlPortIndices();
				for(Integer portIndex : portIndices) {
					String key = (PARAM_LV2_PARAM_PREFIX + portIndex);
					if( parameters.containsKey(key)) {
						this.target.getInstance().setControlPortValue(portIndex, Float.parseFloat(parameters.get(key)));
					}
				}
				
				this.fireParamsEvent(LV2ParamsEvent.ACTION_RESTORE, parameters);
			}
		}
	}
	
	public void storeParameters(Map<String, String> parameters) {
		if( this.isOpen() ) {
			parameters.put(PARAM_LV2_URI, this.target.getPlugin().getUri());
			
			List<Integer> portIndices = this.target.getPlugin().getControlPortIndices();
			for(Integer portIndex : portIndices) {
				parameters.put(PARAM_LV2_PARAM_PREFIX + portIndex, Float.toString(this.target.getInstance().getControlPortValue(portIndex)));
			}
			
			this.appliedParameters = new HashMap<String, String>(parameters);
			
			this.fireParamsEvent(LV2ParamsEvent.ACTION_STORE, parameters);
		}
	}
	
	public void fireParamsEvent(Integer action, Map<String, String> parameters) {
		if( this.isOpen() ) {
			TGEventManager.getInstance(this.context).fireEvent(new LV2ParamsEvent(this.target, action, parameters));
		}
	}
	
	public boolean isBusy() {
		return false;
	}
}
