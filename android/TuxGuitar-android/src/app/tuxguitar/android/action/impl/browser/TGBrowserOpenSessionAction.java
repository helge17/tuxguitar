package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.TGBrowserCollection;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.util.TGContext;

public class TGBrowserOpenSessionAction extends TGActionBase{

	public static final String NAME = "action.browser.open-session";

	public static final String ATTRIBUTE_COLLECTION = TGBrowserCollection.class.getName();

	public TGBrowserOpenSessionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try {
			TGBrowserCollection collection = context.getAttribute(ATTRIBUTE_COLLECTION);
			TGBrowserManager tgBrowserManager = TGBrowserManager.getInstance(getContext());

			if( tgBrowserManager.getSession().getBrowser() != null ) {
				context.setAttribute(TGBrowserCloseAction.ATTRIBUTE_SESSION, tgBrowserManager.getSession());

				TGActionManager.getInstance(getContext()).execute(TGBrowserCloseAction.NAME, context);
			}

			tgBrowserManager.closeSession();
			if( collection != null ) {
				tgBrowserManager.openSession(collection);
			}
		} catch (TGBrowserException e) {
			throw new TGActionException(e);
		}
	}
}
