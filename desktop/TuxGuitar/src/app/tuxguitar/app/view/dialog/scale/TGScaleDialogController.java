package org.herac.tuxguitar.app.view.dialog.scale;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGScaleDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGScaleDialog().show(context);
	}
}
