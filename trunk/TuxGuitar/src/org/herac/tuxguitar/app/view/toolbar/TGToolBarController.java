package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGToolBarController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGToolBar.getInstance(context.getContext()).toogleVisibility();
	}
}
