package app.tuxguitar.app.view.dialog.measure;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureCleanDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasureCleanDialog().show(context);
	}
}
