package app.tuxguitar.app.view.dialog.tempo;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTempoDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTempoDialog().show(context);
	}
}
