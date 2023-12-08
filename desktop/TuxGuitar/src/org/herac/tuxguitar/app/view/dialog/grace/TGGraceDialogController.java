package org.herac.tuxguitar.app.view.dialog.grace;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGGraceDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGGraceDialog().show(context);
	}
}
