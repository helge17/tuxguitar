package org.herac.tuxguitar.app.view.dialog.documentation;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGDocumentationDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGDocumentationDialog().show();
	}
}
