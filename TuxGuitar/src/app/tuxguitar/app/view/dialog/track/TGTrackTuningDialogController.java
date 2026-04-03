package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGTrackTuningDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrackTuningDialog(context).show();
	}
}
