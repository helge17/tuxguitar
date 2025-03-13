package org.herac.tuxguitar.app.view.dialog.stroke;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGStrokeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGStrokeDialog().show(context);
	}
}
