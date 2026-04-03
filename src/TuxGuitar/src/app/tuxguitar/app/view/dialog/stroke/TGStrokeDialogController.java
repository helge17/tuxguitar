package app.tuxguitar.app.view.dialog.stroke;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGStrokeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGStrokeDialog().show(context);
	}
}
