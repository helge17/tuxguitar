package org.herac.tuxguitar.app.view.dialog.printer;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPrinterDataDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPrinterDataDialog().show(context);
	}
}
