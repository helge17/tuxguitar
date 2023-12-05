package org.herac.tuxguitar.app.view.dialog.clef;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGClefDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGClefDialog().show(context);
	}
}
