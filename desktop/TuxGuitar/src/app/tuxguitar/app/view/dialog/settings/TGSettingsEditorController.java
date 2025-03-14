package app.tuxguitar.app.view.dialog.settings;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGSettingsEditorController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGSettingsEditor(context).show();
	}
}
