package org.herac.tuxguitar.app.view.dialog.keybindings;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGKeyBindingEditorController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGKeyBindingEditor(context).show();
	}
}
