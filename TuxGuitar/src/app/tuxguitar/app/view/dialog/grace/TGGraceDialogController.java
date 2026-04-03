package app.tuxguitar.app.view.dialog.grace;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGGraceDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGGraceDialog().show(context);
	}
}
