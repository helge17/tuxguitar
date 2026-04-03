package app.tuxguitar.app.view.dialog.timesignature;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTimeSignatureDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTimeSignatureDialog().show(context);
	}
}
