package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGBrowserSettingsDialogController implements TGDialogController {
	
	public static final String ATTRIBUTE_HANDLER = TGBrowserFactorySettingsHandler.class.getName();
	
	public TGBrowserSettingsDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGBrowserSettingsDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
