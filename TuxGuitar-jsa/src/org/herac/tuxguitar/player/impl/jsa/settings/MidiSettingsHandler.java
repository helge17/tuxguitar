package org.herac.tuxguitar.player.impl.jsa.settings;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class MidiSettingsHandler implements TGPluginSettingsHandler {
	
	private TGContext context;
	
	public MidiSettingsHandler(TGContext context){
		this.context = context;
	}
	
	public void openSettingsDialog(UIWindow parent) {
		MidiConfigUtils.setupDialog(this.context, parent);
	}
}
