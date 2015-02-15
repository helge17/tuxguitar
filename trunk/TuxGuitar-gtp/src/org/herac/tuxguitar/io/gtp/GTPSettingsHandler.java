package org.herac.tuxguitar.io.gtp;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.TGContext;

public class GTPSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;
	
	public GTPSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public void openSettingsDialog(Shell parent) {
		GTPSettingsUtil.getInstance(this.context).configure(parent);
	}
}
