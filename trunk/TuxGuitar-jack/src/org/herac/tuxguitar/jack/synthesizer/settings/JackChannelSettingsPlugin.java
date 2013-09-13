package org.herac.tuxguitar.jack.synthesizer.settings;

import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsPlugin;
import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class JackChannelSettingsPlugin extends TGChannelSettingsPlugin{
	
	private TGChannelSettingsHandler tgChannelSettingsHandler;
	
	public JackChannelSettingsPlugin(){
		this.tgChannelSettingsHandler = new JackChannelSettingsHandler();
	}
	
	protected TGChannelSettingsHandler getHandler() throws TGPluginException {
		return this.tgChannelSettingsHandler;
	}
	
	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
