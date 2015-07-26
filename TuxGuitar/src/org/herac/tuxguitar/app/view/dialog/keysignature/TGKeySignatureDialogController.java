package org.herac.tuxguitar.app.view.dialog.keysignature;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGKeySignatureDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGKeySignatureDialog().show(context);
	}
}
