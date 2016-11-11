package org.herac.tuxguitar.android.storage.browser;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.android.storage.TGStorageProvider;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;

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
