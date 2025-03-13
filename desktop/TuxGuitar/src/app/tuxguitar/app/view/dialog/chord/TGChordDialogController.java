package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGChordDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGChordDialog(context).show();
	}
}
