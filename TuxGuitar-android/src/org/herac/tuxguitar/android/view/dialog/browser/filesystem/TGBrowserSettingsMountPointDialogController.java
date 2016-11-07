package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import android.app.Activity;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.TGDialogUtil;

public class TGBrowserSettingsMountPointDialogController implements TGDialogController {

	public static final String ATTRIBUTE_HANDLER = TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER;

	public TGBrowserSettingsMountPointDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
		TGDialogUtil.showDialog(activity, new TGBrowserSettingsMountPointDialog(), context);
	}
}
