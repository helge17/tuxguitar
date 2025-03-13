package app.tuxguitar.android.storage.browser;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserElement;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.android.storage.TGStorageProvider;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;

public class TGBrowserProvider implements TGStorageProvider {

	private TGContext context;

	public TGBrowserProvider(TGContext context) {
		this.context = context;
		this.createListeners();
	}

	public void createListeners() {
		TGActionManager.getInstance(this.context).addPostExecutionListener(new TGBrowserUpdateFragmentListener(this.context));
	}

	@Override
	public void openDocument() {
		this.createOpenFileAction().process();
	}

	@Override
	public void saveDocument() {
		this.createSaveFileAction().process();
	}

	@Override
	public void saveDocumentAs() {
		this.createSaveFileAsAction().process();
	}

	@Override
	public void updateSession(TGAbstractContext source) {
		TGBrowserSession tgBrowserSession = this.findBrowserSession();
		tgBrowserSession.setCurrentFormat((TGFileFormat) source.getAttribute(TGFileFormat.class.getName()));
		tgBrowserSession.setCurrentElement((TGBrowserElement) source.getAttribute(TGBrowserElement.class.getName()));
	}

	public TGBrowserSession findBrowserSession() {
		return TGBrowserManager.getInstance(this.context).getSession();
	}

	public TGActionProcessor createAction(String actionId) {
		return new TGActionProcessor(this.context, actionId);
	}

	public TGActionProcessor createBrowserAction(String actionId) {
		TGActionProcessor tgActionProcessor = this.createAction(actionId);
		tgActionProcessor.setAttribute(TGBrowserSession.class.getName(), this.findBrowserSession());
		tgActionProcessor.setAttribute(TGActivity.class.getName(), TGActivityController.getInstance(this.context).getActivity());
		return tgActionProcessor;
	}

	public TGActionProcessor createOpenFileAction() {
		return this.createBrowserAction(TGBrowserPrepareForReadAction.NAME);
	}

	public TGActionProcessor createSaveFileAsAction() {
		return this.createBrowserAction(TGBrowserPrepareForWriteAction.NAME);
	}

	public TGActionProcessor createSaveFileAction() {
		return this.createBrowserAction(TGBrowserSaveCurrentElementAction.NAME);
	}
}
