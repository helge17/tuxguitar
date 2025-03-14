package app.tuxguitar.app.view.dialog.info;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGSongInfoDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGSongInfoDialog().show(context);
	}
}
