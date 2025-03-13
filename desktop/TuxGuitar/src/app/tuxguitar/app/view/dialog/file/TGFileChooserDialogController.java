package app.tuxguitar.app.view.dialog.file;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGFileChooserDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGFileChooserDialog().show(context);
	}
}
