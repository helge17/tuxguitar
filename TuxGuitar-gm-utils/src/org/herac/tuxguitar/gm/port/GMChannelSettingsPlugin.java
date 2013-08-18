package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.system.plugins.base.TGChannelSettingsPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GMChannelSettingsPlugin extends TGChannelSettingsPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gm-utils";
	
	private TGChannelSettingsHandler tgChannelSettingsHandler;
	
	public GMChannelSettingsPlugin(){
		this.tgChannelSettingsHandler = new GMChannelSettingsHandler();
	}
	
	protected TGChannelSettingsHandler getHandler() throws TGPluginException {
		return this.tgChannelSettingsHandler;
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
