package org.herac.tuxguitar.app.view.dialog.message;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMessageDialogController implements TGOpenViewController {
	
	public TGMessageDialogController() {
		super();
	}
	
	public void openView(TGViewContext context) {
		new TGMessageDialog().show(context);
	}
}
