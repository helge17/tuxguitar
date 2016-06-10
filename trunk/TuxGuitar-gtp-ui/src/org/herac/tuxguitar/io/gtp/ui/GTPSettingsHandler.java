package org.herac.tuxguitar.io.gtp.ui;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class GTPSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;
	
	public GTPSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public void openSettingsDialog(UIWindow parent) {
		new GTPSettingsDialog(this.context).configure(parent);
	}
}
