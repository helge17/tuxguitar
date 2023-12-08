package org.herac.tuxguitar.app.view.dialog.trill;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTrillDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrillDialog().show(context);
	}
}
