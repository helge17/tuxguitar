package app.tuxguitar.app.view.dialog.printer;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGPrintPreviewDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGPrintPreviewDialog(context).show();
	}
}
