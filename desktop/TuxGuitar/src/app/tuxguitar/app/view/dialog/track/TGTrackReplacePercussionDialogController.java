package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGTrackReplacePercussionDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTrackReplacePercussionDialog(context).show();
	}
}
