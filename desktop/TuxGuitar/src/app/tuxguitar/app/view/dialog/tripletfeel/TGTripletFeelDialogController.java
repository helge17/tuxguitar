package app.tuxguitar.app.view.dialog.tripletfeel;

import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.controller.TGOpenViewController;

public class TGTripletFeelDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGTripletFeelDialog().show(context);
	}
}
