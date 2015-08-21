package org.herac.tuxguitar.jack.synthesizer.settings;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
