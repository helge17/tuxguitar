package org.herac.tuxguitar.app.view.dialog.transpose;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTransposeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTransposeDialog().show(context);
	}
}
