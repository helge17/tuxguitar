package org.herac.tuxguitar.midi.synth.ui;

import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import org.herac.tuxguitar.midi.synth.TGSynthPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGSynthDialogPlugin extends TGChannelSettingsPlugin{
	
	public TGSynthDialogPlugin(){
		super();
	}
	
	protected TGChannelSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new TGSynthDialogHandler(context);
	}
	
	public String getModuleId(){
		return TGSynthPlugin.MODULE_ID;
	}
}
