package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGMainToolBarController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGMainToolBar.getInstance(context.getContext()).toogleVisibility();
	}
}
