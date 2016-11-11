package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserLoadSessionAction extends TGActionBase{
	
	public static final String NAME = "action.browser.load-session";
	
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	public static final String ATTRIBUTE_COLLECTION = TGBrowserCollection.class.getName();
	public static final String ATTRIBUTE_BROWSER = TGBrowser.class.getName();
	
	public TGBrowserLoadSessionAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		final TGBrowser tgBrowser = context.getAttribute(ATTRIBUTE_BROWSER);
		final TGBrowserCollection tgBrowserCollection = context.getAttribute(ATTRIBUTE_COLLECTION);
		final TGBrowserSession tgBrowserSession = context.getAttribute(ATTRIBUTE_SESSION);

		tgBrowserSession.setBrowser(tgBrowser);
		if( tgBrowserSession.getBrowser() != null ) {
			tgBrowserSession.getBrowser().open(new TGBrowserActionCallBack<Object>(this, context) {
				public void onActionSuccess(TGActionContext context, Object successData) {
					tgBrowserSession.getBrowser().cdRoot(new TGBrowserActionCallBack<Object>(TGBrowserLoadSessionAction.this, context) {
						public void onActionSuccess(TGActionContext context, Object successData) {
							try {
								tgBrowserSession.setCollection(tgBrowserCollection);

								TGBrowserManager.getInstance(getContext()).storeDefaultCollection();
								TGActionManager.getInstance(getContext()).execute(TGBrowserRefreshAction.NAME, context);
							} catch (TGBrowserException e)  {
								throw new TGActionException(e);
							}
						}
					});
				}
			});
		}
	}
}
