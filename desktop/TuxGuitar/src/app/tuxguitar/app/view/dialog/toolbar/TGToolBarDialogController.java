package app.tuxguitar.app.view.dialog.toolbar;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGToolBarDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMainToolBarDialog().show(context);
	}
}
