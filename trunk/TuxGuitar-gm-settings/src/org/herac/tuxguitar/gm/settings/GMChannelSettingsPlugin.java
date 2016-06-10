package org.herac.tuxguitar.gm.settings;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GMChannelSettingsPlugin extends TGChannelSettingsPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gm-settings";
	
	public GMChannelSettingsPlugin(){
		super(); 
	}
	
	protected TGChannelSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new GMChannelSettingsHandler(context);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
