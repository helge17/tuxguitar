package org.herac.tuxguitar.app.view.dialog.about;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGAboutDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGAboutDialog().show(context);
	}
}
