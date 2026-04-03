package app.tuxguitar.app.view.dialog.tremolobar;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGTremoloBarDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTremoloBarDialog().show(context);
	}
}
