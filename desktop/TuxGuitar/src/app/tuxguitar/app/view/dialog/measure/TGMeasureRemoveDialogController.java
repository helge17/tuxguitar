package app.tuxguitar.app.view.dialog.measure;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureRemoveDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasureRemoveDialog().show(context);
	}
}
