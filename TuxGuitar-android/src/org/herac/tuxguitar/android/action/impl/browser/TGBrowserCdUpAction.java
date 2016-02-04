package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.TGBrowserSyncCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserCdUpAction extends TGActionBase{
	
	public static final String NAME = "action.browser.cd-up";
	
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserCdUpAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		try {
			TGBrowserSession tgBrowserSession = (TGBrowserSession) context.getAttribute(ATTRIBUTE_SESSION);
			if( tgBrowserSession.getBrowser() != null ) {
				TGBrowserSyncCallBack<Object> tgBrowserSyncCallBack = new TGBrowserSyncCallBack<Object>();
				tgBrowserSession.getBrowser().cdUp(tgBrowserSyncCallBack);
				tgBrowserSyncCallBack.syncCallBack();
			}
		} catch (TGBrowserException e)  {
			throw new TGActionException(e);
		}
	}
}
