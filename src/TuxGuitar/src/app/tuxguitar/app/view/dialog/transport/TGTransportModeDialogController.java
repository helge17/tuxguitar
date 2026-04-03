package app.tuxguitar.app.view.dialog.transport;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGTransportModeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTransportModeDialog(context).show();
	}
}
