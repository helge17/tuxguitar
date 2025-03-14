package app.tuxguitar.gm.settings;

import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

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
