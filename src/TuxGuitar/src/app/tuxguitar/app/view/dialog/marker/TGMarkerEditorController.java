package app.tuxguitar.app.view.dialog.marker;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGMarkerEditorController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMarkerEditor(context).show();
	}
}
