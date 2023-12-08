package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import org.herac.tuxguitar.android.view.dialog.fragment.TGDialogFragmentController;

public class TGBrowserSettingsMountPointDialogController extends TGDialogFragmentController<TGBrowserSettingsMountPointDialog> {

	public static final String ATTRIBUTE_HANDLER = TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER;

	public TGBrowserSettingsMountPointDialogController() {
		super();
	}

	@Override
	public TGBrowserSettingsMountPointDialog createNewInstance() {
		return new TGBrowserSettingsMountPointDialog();
	}
}
