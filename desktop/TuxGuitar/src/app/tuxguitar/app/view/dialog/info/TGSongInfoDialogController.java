package org.herac.tuxguitar.app.view.dialog.info;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGSongInfoDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGSongInfoDialog().show(context);
	}
}
