package app.tuxguitar.app.view.dialog.documentation;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGDocumentationDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGDocumentationDialog(context).show();
	}
}
