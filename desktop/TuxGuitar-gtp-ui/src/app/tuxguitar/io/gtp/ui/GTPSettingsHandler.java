package app.tuxguitar.io.gtp.ui;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class GTPSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;

	public GTPSettingsHandler(TGContext context) {
		this.context = context;
	}

	public void openSettingsDialog(UIWindow parent) {
		new GTPSettingsDialog(this.context).configure(parent);
	}
}
