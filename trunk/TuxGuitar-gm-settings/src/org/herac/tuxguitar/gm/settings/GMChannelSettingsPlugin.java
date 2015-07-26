package org.herac.tuxguitar.gm.settings;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GMChannelSettingsPlugin extends TGChannelSettingsPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gm-settings";
	
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
