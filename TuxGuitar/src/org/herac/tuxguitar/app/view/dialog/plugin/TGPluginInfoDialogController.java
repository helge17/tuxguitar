package org.herac.tuxguitar.app.view.dialog.plugin;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPluginInfoDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPluginInfoDialog().show(context);
	}
}
