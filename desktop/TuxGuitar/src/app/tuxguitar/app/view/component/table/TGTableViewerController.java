package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGTableViewerController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGTableViewer.getInstance(context.getContext()).toggleVisibility();
	}
}
