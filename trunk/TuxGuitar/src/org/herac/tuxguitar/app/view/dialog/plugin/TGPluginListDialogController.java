package org.herac.tuxguitar.app.view.dialog.plugin;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGPluginListDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPluginListDialog().show(context);
	}
}
