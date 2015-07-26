package org.herac.tuxguitar.app.view.dialog.tripletfeel;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTripletFeelDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTripletFeelDialog().show(context);
	}
}
