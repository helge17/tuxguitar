package app.tuxguitar.android.view.dialog.browser.filesystem;

import app.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGBrowserSettingsDialogController extends TGModalFragmentController<TGBrowserSettingsDialog> {

	public static final String ATTRIBUTE_HANDLER = TGBrowserFactorySettingsHandler.class.getName();
	public static final String ATTRIBUTE_MOUNT_POINT = TGBrowserSettingsMountPoint.class.getName();

	public TGBrowserSettingsDialogController() {
		super();
	}

	@Override
	public TGBrowserSettingsDialog createNewInstance() {
		return new TGBrowserSettingsDialog();
	}
}
