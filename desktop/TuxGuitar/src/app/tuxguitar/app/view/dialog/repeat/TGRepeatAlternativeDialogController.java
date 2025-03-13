package app.tuxguitar.app.view.dialog.repeat;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGRepeatAlternativeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGRepeatAlternativeDialog().show(context);
	}
}
