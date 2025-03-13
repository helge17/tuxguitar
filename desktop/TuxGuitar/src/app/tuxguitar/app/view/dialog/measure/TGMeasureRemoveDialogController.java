package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureRemoveDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasureRemoveDialog().show(context);
	}
}
