package org.herac.tuxguitar.android.browser;

import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

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
	public void onCreateBrowser(TGBrowser browser) throws TGBrowserException {
		this.session.setBrowser(browser);
		if( this.session.getBrowser() != null ) {
			this.session.getBrowser().open();
			this.session.getBrowser().cdRoot();
			this.session.setCollection(this.collection);
			this.storeDefaultCollection();
			this.fireRefreshAction();
		}
	}
	
	public void storeDefaultCollection() throws TGBrowserException {
		TGBrowserManager.getInstance(this.context).storeDefaultCollection();
	}
	
	public void fireRefreshAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGBrowserRefreshAction.NAME);
		tgActionProcessor.setAttribute(TGBrowserRefreshAction.ATTRIBUTE_SESSION, this.session);
		tgActionProcessor.process();
	}
}
