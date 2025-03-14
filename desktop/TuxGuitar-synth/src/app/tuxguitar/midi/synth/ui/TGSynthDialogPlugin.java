package app.tuxguitar.midi.synth.ui;

import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsHandler;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsPlugin;
import app.tuxguitar.midi.synth.TGSynthPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

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
