package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGMainToolBarController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGMainToolBar.getInstance(context.getContext()).toogleVisibility();
	}
}
