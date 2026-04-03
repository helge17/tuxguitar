package app.tuxguitar.app.view.dialog.keysignature;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGKeySignatureDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGKeySignatureDialog().show(context);
	}
}
