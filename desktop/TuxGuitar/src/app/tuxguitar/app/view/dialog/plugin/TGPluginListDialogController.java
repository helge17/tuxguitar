package app.tuxguitar.app.view.dialog.plugin;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGPluginListDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPluginListDialog().show(context);
	}
}
