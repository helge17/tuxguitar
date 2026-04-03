package app.tuxguitar.app.view.dialog.scale;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGScaleDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGScaleDialog().show(context);
	}
}
