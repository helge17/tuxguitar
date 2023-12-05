package org.herac.tuxguitar.app.view.dialog.settings;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGSettingsEditorController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGSettingsEditor(context).show();
	}
}
