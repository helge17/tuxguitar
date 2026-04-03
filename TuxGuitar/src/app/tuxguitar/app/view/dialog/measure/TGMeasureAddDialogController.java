package app.tuxguitar.app.view.dialog.measure;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureAddDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasureAddDialog().show(context);
	}
}
