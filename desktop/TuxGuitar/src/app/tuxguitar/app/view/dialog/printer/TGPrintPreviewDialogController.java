package org.herac.tuxguitar.app.view.dialog.printer;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPrintPreviewDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPrintPreviewDialog(context).show();
	}
}
