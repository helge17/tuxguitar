package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import android.app.Activity;

import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.TGDialogUtil;

public class TGBrowserSettingsDialogController implements TGDialogController {
	
	public static final String ATTRIBUTE_HANDLER = TGBrowserFactorySettingsHandler.class.getName();
	public static final String ATTRIBUTE_MOUNT_POINT = TGBrowserSettingsMountPoint.class.getName();
	
	public TGBrowserSettingsDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
		TGDialogUtil.showDialog(activity, new TGBrowserSettingsDialog(), context);
	}
}
