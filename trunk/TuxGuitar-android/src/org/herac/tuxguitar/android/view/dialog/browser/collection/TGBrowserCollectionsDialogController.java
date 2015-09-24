package org.herac.tuxguitar.android.view.dialog.browser.collection;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGBrowserCollectionsDialogController implements TGDialogController {
	
	public TGBrowserCollectionsDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGBrowserCollectionsDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
