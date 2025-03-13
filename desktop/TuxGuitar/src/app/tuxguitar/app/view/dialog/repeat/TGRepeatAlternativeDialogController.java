package org.herac.tuxguitar.app.view.dialog.repeat;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGRepeatAlternativeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGRepeatAlternativeDialog().show(context);
	}
}
