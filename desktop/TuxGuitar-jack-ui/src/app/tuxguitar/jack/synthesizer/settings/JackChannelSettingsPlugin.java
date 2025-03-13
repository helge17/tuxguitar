package app.tuxguitar.jack.synthesizer.settings;

import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import app.tuxguitar.jack.JackPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class JackChannelSettingsPlugin extends TGChannelSettingsPlugin{

	private TGChannelSettingsHandler tgChannelSettingsHandler;

	public JackChannelSettingsPlugin(){
		super();
	}

	protected TGChannelSettingsHandler createHandler(TGContext context) throws TGPluginException {
		if( this.tgChannelSettingsHandler == null ) {
			this.tgChannelSettingsHandler = new JackChannelSettingsHandler(context);
		}
		return this.tgChannelSettingsHandler;
	}

	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
