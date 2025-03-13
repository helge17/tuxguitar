package app.tuxguitar.android.view.browser.filesystem;

import android.app.Activity;

import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.browser.filesystem.TGFsBrowserSettingsFactory;
import app.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import app.tuxguitar.android.view.dialog.browser.filesystem.TGBrowserSettingsDialogController;
import app.tuxguitar.android.view.dialog.browser.filesystem.TGBrowserSettingsMountPointDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

public class TGBrowserSettingsFactoryImpl implements TGFsBrowserSettingsFactory {

	private TGContext context;
	private Activity activity;

	public TGBrowserSettingsFactoryImpl(TGContext context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}

	@Override
	public void createSettings(TGBrowserFactorySettingsHandler handler) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGBrowserSettingsMountPointDialogController());
		tgActionProcessor.setAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER, handler);
		tgActionProcessor.process();
	}
}
