package org.herac.tuxguitar.app.view.dialog.bend;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGBendDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGBendDialog().show(context);
	}
}
