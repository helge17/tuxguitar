package app.tuxguitar.app.view.dialog.text;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTextDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTextDialog().show(context);
	}
}
