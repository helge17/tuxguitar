package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.util.TGContext;

public class TGBrowserCloseAction extends TGActionBase{

	public static final String NAME = "action.browser.close";

	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();

	public TGBrowserCloseAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGBrowserSession tgBrowserSession = context.getAttribute(ATTRIBUTE_SESSION);
		if( tgBrowserSession.getBrowser() != null ) {
			tgBrowserSession.getBrowser().close(new TGBrowserActionCallBack<Object>(this, context) {
				public void onActionSuccess(TGActionContext context, Object successData) {
					// nothing to do
				}
			});
		}
	}
}
