package app.tuxguitar.app.view.dialog.tremolopicking;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTremoloPickingDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTremoloPickingDialog().show(context);
	}
}
