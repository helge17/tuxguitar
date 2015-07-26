package org.herac.tuxguitar.app.view.items.controller;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.toolbar.TGToolBar;

public class TGToolBarController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGToolBar.getInstance(context.getContext()).toogleVisibility();
	}
}
