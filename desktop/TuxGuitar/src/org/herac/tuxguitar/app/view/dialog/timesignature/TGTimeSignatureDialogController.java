package org.herac.tuxguitar.app.view.dialog.timesignature;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTimeSignatureDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTimeSignatureDialog().show(context);
	}
}
