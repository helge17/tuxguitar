package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGTrackStringCountDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrackStringCountDialog().show(context);
	}
}
