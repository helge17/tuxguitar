package app.tuxguitar.app.view.dialog.printer;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGPrinterChooserDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPrinterChooserDialog().show(context);
	}
}
