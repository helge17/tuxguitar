package org.herac.tuxguitar.android.view.dialog.browser.collection;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.TGDialogUtil;

import android.app.Activity;

public class TGBrowserCollectionsDialogController implements TGDialogController {
	
	public TGBrowserCollectionsDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        TGDialogUtil.showDialog(activity, new TGBrowserCollectionsDialog(), context);
	}
}
