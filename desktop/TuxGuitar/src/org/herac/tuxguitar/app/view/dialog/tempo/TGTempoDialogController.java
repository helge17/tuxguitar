package org.herac.tuxguitar.app.view.dialog.tempo;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTempoDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTempoDialog().show(context);
	}
}
