package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGTrackPropertiesDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrackPropertiesDialog(context).show();
	}
}
