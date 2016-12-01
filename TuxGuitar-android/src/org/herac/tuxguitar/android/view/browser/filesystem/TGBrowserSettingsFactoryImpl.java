package org.herac.tuxguitar.android.view.browser.filesystem;

import android.app.Activity;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.browser.filesystem.TGFsBrowserSettingsFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.view.dialog.browser.filesystem.TGBrowserSettingsDialogController;
import org.herac.tuxguitar.android.view.dialog.browser.filesystem.TGBrowserSettingsMountPointDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

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
