package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class VSTSettings {
	
	private static final String VST_PLUGIN_PATH = "vst.plugin.path";
	
	private TGContext context;
	private TGConfigManager config;
	
	public VSTSettings(TGContext context){
		this.context = context;
	}
	
	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-synth-vst");
		}
		return this.config;
	}
	
	public void save(){
		this.getConfig().save();
	}
	
	public String getPluginPath() {
		return this.getConfig().getStringValue(VST_PLUGIN_PATH);
	}
	
	public void setPluginPath(String pluginPath) {
		this.getConfig().setValue(VST_PLUGIN_PATH, pluginPath);
	}
}
