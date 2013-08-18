package org.herac.tuxguitar.io.gtp;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;

public class GTPSettingsHandler implements TGPluginSettingsHandler {

	public void openSettingsDialog(Shell parent) {
		GTPSettingsUtil.instance().configure(parent);
	}
}
