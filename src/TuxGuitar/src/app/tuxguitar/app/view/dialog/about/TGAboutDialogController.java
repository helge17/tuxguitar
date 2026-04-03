package app.tuxguitar.app.view.dialog.about;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGAboutDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGAboutDialog().show(context);
	}
}
