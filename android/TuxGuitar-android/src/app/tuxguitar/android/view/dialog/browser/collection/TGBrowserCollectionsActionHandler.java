package app.tuxguitar.android.view.dialog.browser.collection;

import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.tools.browser.TGBrowserCollection;

public class TGBrowserCollectionsActionHandler {

	private TGBrowserCollectionsDialog view;

	public TGBrowserCollectionsActionHandler(TGBrowserCollectionsDialog view) {
		this.view = view;
	}

	public TGActionProcessorListener createAction(String actionId) {
		return new TGActionProcessorListener(this.view.findContext(), actionId);
	}

	public TGActionProcessorListener createAddCollectionAction(TGBrowserCollection collection) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGBrowserAddCollectionAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserAddCollectionAction.ATTRIBUTE_COLLECTION, collection);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createRemoveCollectionAction(TGBrowserCollection collection) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGBrowserRemoveCollectionAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserRemoveCollectionAction.ATTRIBUTE_COLLECTION, collection);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createOpenDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.view.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createOpenMenuAction(TGMenuController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.view.findActivity());
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		return tgActionProcessor;
	}
}
