package org.herac.tuxguitar.app.view.dialog.marker;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGMarkerEditorController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMarkerEditor(context).show();
	}
}
