package org.herac.tuxguitar.android.view.browser.filesystem;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.browser.filesystem.TGFsBrowserSettingsFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.view.dialog.browser.filesystem.TGBrowserSettingsDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

import android.app.Activity;

public class TGBrowserSettingsFactoryImpl implements TGFsBrowserSettingsFactory {
	
	private TGContext context;
	private Activity activity;
	
	public TGBrowserSettingsFactoryImpl(TGContext context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}
	
	@Override
	public void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGBrowserSettingsDialogController());
		tgActionProcessor.setAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER, handler);
		tgActionProcessor.process();
	}
}
