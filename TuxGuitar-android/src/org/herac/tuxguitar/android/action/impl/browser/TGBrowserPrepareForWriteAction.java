package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserPrepareForWriteAction extends TGActionBase{
	
	public static final String NAME = "action.browser.prepare-for-write";
	
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserPrepareForWriteAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext tgActionContext) {
		TGBrowserSession tgBrowserSession = tgActionContext.getAttribute(ATTRIBUTE_SESSION);
		tgBrowserSession.setSessionType(TGBrowserSession.WRITE_MODE);
	}
}
