package org.herac.tuxguitar.app.view.dialog.text;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTextDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTextDialog().show(context);
	}
}
