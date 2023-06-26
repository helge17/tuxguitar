package org.herac.tuxguitar.app.view.dialog.file;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGCustomTemplateDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGCustomTemplateDialog().show(context);
	}
}
