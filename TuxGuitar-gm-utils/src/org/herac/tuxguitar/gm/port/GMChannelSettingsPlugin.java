package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.app.editors.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.system.plugins.TGPluginException;
import org.herac.tuxguitar.app.system.plugins.base.TGChannelSettingsPlugin;

public class GMChannelSettingsPlugin extends TGChannelSettingsPlugin{
	
	private TGChannelSettingsHandler tgChannelSettingsHandler;
	
	public GMChannelSettingsPlugin(){
		this.tgChannelSettingsHandler = new GMChannelSettingsHandler();
	}
	
	protected TGChannelSettingsHandler getHandler() throws TGPluginException {
		return this.tgChannelSettingsHandler;
	}
}
