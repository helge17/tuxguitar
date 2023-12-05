package org.herac.tuxguitar.app.view.dialog.tremolopicking;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTremoloPickingDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTremoloPickingDialog().show(context);
	}
}
