package app.tuxguitar.app.view.dialog.trill;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTrillDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrillDialog().show(context);
	}
}
