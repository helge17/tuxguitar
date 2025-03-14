package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.TGBrowserCollection;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowser;
import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.util.TGContext;

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
