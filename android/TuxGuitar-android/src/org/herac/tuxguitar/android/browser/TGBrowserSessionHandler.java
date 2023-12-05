package org.herac.tuxguitar.android.browser;

import org.herac.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGBrowserSessionHandler implements TGBrowserFactoryHandler {
	
	private TGContext context;
	private TGBrowserSession session;
	private TGBrowserCollection collection;
	
	public TGBrowserSessionHandler(TGContext context, TGBrowserSession session, TGBrowserCollection collection) {
		this.context = context;
		this.session = session;
		this.collection = collection;
	}
	
	@Override
	public void onCreateBrowser(TGBrowser browser) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGBrowserLoadSessionAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserLoadSessionAction.ATTRIBUTE_SESSION, this.session);
		tgActionProcessor.setAttribute(TGBrowserLoadSessionAction.ATTRIBUTE_COLLECTION, this.collection);
		tgActionProcessor.setAttribute(TGBrowserLoadSessionAction.ATTRIBUTE_BROWSER, browser);
		tgActionProcessor.process();
	}

	@Override
	public void handleError(Throwable throwable) {
		TGErrorManager.getInstance(this.context).handleError(throwable);
	}
}
