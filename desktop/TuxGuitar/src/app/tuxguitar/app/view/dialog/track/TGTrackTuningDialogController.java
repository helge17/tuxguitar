package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGTrackTuningDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrackTuningDialog(context).show();
	}
}
