package app.tuxguitar.app.view.toolbar.edit;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGEditToolBarController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGEditToolBar.getInstance(context.getContext()).toogleVisibility();
	}
}
