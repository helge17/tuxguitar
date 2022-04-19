package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class VSTSettings {
	
	private static final String VST_PLUGIN_PATH = "vst.plugin.path";
	private static final String VST_PLUGIN_EXTENSIONS = "vst.plugin.extensions";
	private static final String VST_PLUGIN_CLIENT_COMMAND_PREFIX = "vst.plugin.client.command.";
	
	private TGContext context;
	private TGConfigManager config;
	
	public VSTSettings(TGContext context){
		this.context = context;
	}
	
	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-synth-vst-remote");
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
	
	public String[] getPluginExtensions() {
		String extensions = this.getConfig().getStringValue(VST_PLUGIN_EXTENSIONS);
		if( extensions != null ) {
			return extensions.split(";");
		}
		return null;
	}
	
	public String getPluginClientCommand(String pluginType) {
		return this.getConfig().getStringValue(VST_PLUGIN_CLIENT_COMMAND_PREFIX + pluginType);
	}
}
