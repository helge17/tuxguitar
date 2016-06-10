package org.herac.tuxguitar.app.view.dialog.marker;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGMarkerListController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGMarkerList view = TGMarkerList.getInstance(context.getContext());
		if( view.isDisposed()){
			view.show(context);
		} else {
			view.dispose();
		}
	}
}
