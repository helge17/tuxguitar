package org.herac.tuxguitar.app.view.dialog.transport;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGTransportModeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTransportModeDialog(context).show();
	}
}
