package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGNewTrackDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGNewTrackDialog(context).show();
	}
}
