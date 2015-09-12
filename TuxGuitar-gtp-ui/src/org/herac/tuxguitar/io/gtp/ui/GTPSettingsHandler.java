package org.herac.tuxguitar.io.gtp.ui;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.TGContext;

public class GTPSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;
	
	public GTPSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public void openSettingsDialog(Shell parent) {
		new GTPSettingsDialog(this.context).configure(parent);
	}
}
