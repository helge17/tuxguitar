package app.tuxguitar.app.view.dialog.repeat;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGRepeatCloseDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGRepeatCloseDialog().show(context);
	}
}
