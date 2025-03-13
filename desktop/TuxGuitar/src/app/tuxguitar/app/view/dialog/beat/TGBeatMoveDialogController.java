package app.tuxguitar.app.view.dialog.beat;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGBeatMoveDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGBeatMoveDialog().show(context);
	}
}
