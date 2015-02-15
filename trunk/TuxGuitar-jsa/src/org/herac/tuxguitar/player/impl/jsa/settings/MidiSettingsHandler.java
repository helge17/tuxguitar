package org.herac.tuxguitar.player.impl.jsa.settings;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;
import org.herac.tuxguitar.util.TGContext;

public class MidiSettingsHandler implements TGPluginSettingsHandler {
	
	private TGContext context;
	
	public MidiSettingsHandler(TGContext context){
		this.context = context;
	}
	
	public void openSettingsDialog(Shell parent) {
		MidiConfigUtils.setupDialog(this.context, parent);
	}
}
