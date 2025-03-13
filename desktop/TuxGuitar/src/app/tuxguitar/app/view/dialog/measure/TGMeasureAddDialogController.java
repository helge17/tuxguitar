package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureAddDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasureAddDialog().show(context);
	}
}
