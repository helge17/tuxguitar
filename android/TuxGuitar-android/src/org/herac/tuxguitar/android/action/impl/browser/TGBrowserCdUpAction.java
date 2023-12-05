package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserCdUpAction extends TGActionBase{
	
	public static final String NAME = "action.browser.cd-up";
	
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserCdUpAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGBrowserSession tgBrowserSession = context.getAttribute(ATTRIBUTE_SESSION);
		if( tgBrowserSession.getBrowser() != null ) {
			tgBrowserSession.getBrowser().cdUp(new TGBrowserActionCallBack<Object>(this, context) {
				public void onActionSuccess(TGActionContext context, Object successData) {
					// nothing to do
				}
			});
		}
	}
}
