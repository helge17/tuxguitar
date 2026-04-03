package app.tuxguitar.app.view.dialog.chord;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGChordDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGChordDialog(context).show();
	}
}
