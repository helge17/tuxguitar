package app.tuxguitar.app.view.dialog.confirm;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGConfirmDialogController implements TGOpenViewController {

	public TGConfirmDialogController() {
		super();
	}

	public void openView(TGViewContext context) {
		new TGConfirmDialog().show(context);
	}
}
