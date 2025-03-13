package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.util.TGContext;

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
