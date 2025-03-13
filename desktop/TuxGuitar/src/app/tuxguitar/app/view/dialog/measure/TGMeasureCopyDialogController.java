package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureCopyDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasureCopyDialog().show(context);
	}
}
