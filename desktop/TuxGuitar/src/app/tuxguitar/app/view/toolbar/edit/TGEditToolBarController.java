package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGEditToolBarController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGEditToolBar.getInstance(context.getContext()).toogleVisibility();
	}
}
