package org.herac.tuxguitar.app.view.dialog.browser.main;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGBrowserDialogController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGBrowserDialog dialog = TGBrowserDialog.getInstance(context.getContext());
		if( dialog.isDisposed()){
			dialog.show();
		} else {
			dialog.dispose();
		}
	}
}
