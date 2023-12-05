package org.herac.tuxguitar.app.view.dialog.confirm;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGConfirmDialogController implements TGOpenViewController {
	
	public TGConfirmDialogController() {
		super();
	}
	
	public void openView(TGViewContext context) {
		new TGConfirmDialog().show(context);
	}
}
