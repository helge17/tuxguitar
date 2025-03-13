package org.herac.tuxguitar.app.view.dialog.harmonic;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGHarmonicDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGHarmonicDialog().show(context);
	}
}
