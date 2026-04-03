package app.tuxguitar.app.view.dialog.bend;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGBendDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGBendDialog().show(context);
	}
}
