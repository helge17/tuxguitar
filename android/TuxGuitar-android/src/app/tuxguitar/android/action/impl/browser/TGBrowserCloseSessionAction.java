package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.util.TGContext;

public class TGBrowserCloseSessionAction extends TGActionBase{

	public static final String NAME = "action.browser.close-session";

	public TGBrowserCloseSessionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try {
			TGBrowserManager tgBrowserManager = TGBrowserManager.getInstance(getContext());

			if( tgBrowserManager.getSession().getBrowser() != null ) {
				context.setAttribute(TGBrowserCloseAction.ATTRIBUTE_SESSION, tgBrowserManager.getSession());

				TGActionManager.getInstance(getContext()).execute(TGBrowserCloseAction.NAME, context);
			}

			tgBrowserManager.closeSession();
		} catch (TGBrowserException e) {
			throw new TGActionException(e);
		}
	}
}
