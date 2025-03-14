package app.tuxguitar.app.view.dialog.message;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMessageDialogController implements TGOpenViewController {

	public TGMessageDialogController() {
		super();
	}

	public void openView(TGViewContext context) {
		new TGMessageDialog().show(context);
	}
}
