package app.tuxguitar.app.view.dialog.keybindings;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGKeyBindingEditorController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGKeyBindingEditor(context).show();
	}
}
