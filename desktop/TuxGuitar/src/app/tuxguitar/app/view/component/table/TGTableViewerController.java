package app.tuxguitar.app.view.component.table;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGTableViewerController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGTableViewer.getInstance(context.getContext()).toggleVisibility();
	}
}
