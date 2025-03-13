package org.herac.tuxguitar.app.view.dialog.file;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGOpenUrlDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGOpenUrlDialog().show(context);
	}
}
