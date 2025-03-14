package app.tuxguitar.android.view.browser;

import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.browser.TGBrowserCollection;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserElement;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.io.base.TGFileFormat;

public class TGBrowserActionHandler {

	private TGBrowserView browser;

	public TGBrowserActionHandler(TGBrowserView browser) {
		this.browser = browser;
	}

	public TGActionProcessorListener createAction(String actionId) {
		return new TGActionProcessorListener(this.browser.findContext(), actionId);
	}

	public TGActionProcessorListener createBrowserAction(String actionId) {
		TGBrowserSession tgBrowserSession = TGBrowserManager.getInstance(this.browser.findContext()).getSession();
		TGActionProcessorListener tgActionProcessor = this.createAction(actionId);
		tgActionProcessor.setAttribute(TGBrowserSession.class.getName(), tgBrowserSession);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createBrowserElementAction(String actionId, TGBrowserElement element) {
		TGActionProcessorListener tgActionProcessor = this.createBrowserAction(actionId);
		tgActionProcessor.setAttribute(TGBrowserElement.class.getName(), element);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createBrowserOpenElementAction(TGBrowserElement element, String formatCode) {
		TGActionProcessorListener tgActionProcessor = this.createBrowserElementAction(TGBrowserOpenElementAction.NAME, element);
		tgActionProcessor.setAttribute(TGBrowserOpenElementAction.ATTRIBUTE_FORMAT_CODE, formatCode);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createBrowserSaveElementAction(TGBrowserElement element, String formatCode) {
		TGActionProcessorListener tgActionProcessor = this.createBrowserElementAction(TGBrowserSaveElementAction.NAME, element);
		tgActionProcessor.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT_CODE, formatCode);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createBrowserSaveElementAction(TGBrowserElement element, TGFileFormat format) {
		TGActionProcessorListener tgActionProcessor = this.createBrowserElementAction(TGBrowserSaveElementAction.NAME, element);
		tgActionProcessor.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT, format);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createBrowserSaveNewElementAction(String elementName, TGFileFormat format) {
		TGActionProcessorListener tgActionProcessor = this.createBrowserAction(TGBrowserSaveNewElementAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT, format);
		tgActionProcessor.setAttribute(TGBrowserSaveNewElementAction.ATTRIBUTE_NAME, elementName);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createOpenSessionAction(TGBrowserCollection collection) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGBrowserOpenSessionAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserOpenSessionAction.ATTRIBUTE_COLLECTION, collection);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createCloseSessionAction() {
		return this.createAction(TGBrowserCloseSessionAction.NAME);
	}

	public TGActionProcessorListener createOpenDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.browser.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public void processConfirmableAction(final TGActionProcessor actionProcessor, final String confirmMessage) {
		TGActionProcessor tgActionProcessor = this.createOpenDialogAction(new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				actionProcessor.process();
			}
		});
		tgActionProcessor.process();
	}
}
