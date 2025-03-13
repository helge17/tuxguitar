package app.tuxguitar.app.view.dialog.plugin;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGPluginInfoDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPluginInfoDialog().show(context);
	}
}
