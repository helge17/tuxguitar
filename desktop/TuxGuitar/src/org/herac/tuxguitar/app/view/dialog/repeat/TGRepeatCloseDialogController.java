package org.herac.tuxguitar.app.view.dialog.repeat;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGRepeatCloseDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGRepeatCloseDialog().show(context);
	}
}
