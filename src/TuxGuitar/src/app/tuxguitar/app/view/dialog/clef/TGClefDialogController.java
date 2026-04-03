package app.tuxguitar.app.view.dialog.clef;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGClefDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGClefDialog().show(context);
	}
}
