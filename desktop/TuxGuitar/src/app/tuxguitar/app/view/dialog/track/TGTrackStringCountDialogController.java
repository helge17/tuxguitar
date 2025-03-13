package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGTrackStringCountDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrackStringCountDialog().show(context);
	}
}
