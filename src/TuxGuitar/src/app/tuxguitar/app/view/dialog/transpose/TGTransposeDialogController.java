package app.tuxguitar.app.view.dialog.transpose;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTransposeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTransposeDialog().show(context);
	}
}
