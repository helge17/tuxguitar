package app.tuxguitar.app.view.dialog.printer;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGPrintSettingsDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPrintSettingsDialog().show(context);
	}
}
