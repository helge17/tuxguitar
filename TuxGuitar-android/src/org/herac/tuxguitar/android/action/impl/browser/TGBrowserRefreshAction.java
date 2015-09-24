package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserRefreshAction extends TGActionBase{
	
	public static final String NAME = "action.browser.refresh";
	
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserRefreshAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		try {
			TGBrowserSession tgBrowserSession = (TGBrowserSession) context.getAttribute(ATTRIBUTE_SESSION);
			if( tgBrowserSession.getBrowser() != null ) {
				tgBrowserSession.setCurrentElements(tgBrowserSession.getBrowser().listElements());
			}
		} catch (TGBrowserException e)  {
			throw new TGActionException(e);
		}
	}
}
