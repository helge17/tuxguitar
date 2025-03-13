package org.herac.tuxguitar.app.view.dialog.beat;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGBeatMoveDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGBeatMoveDialog().show(context);
	}
}
