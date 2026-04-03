package app.tuxguitar.app.view.dialog.harmonic;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGHarmonicDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGHarmonicDialog().show(context);
	}
}
