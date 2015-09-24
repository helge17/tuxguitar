package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserOpenSessionAction extends TGActionBase{
	
	public static final String NAME = "action.browser.open-session";
	
	public static final String ATTRIBUTE_COLLECTION = TGBrowserCollection.class.getName();
	
	public TGBrowserOpenSessionAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		try {
			TGBrowserCollection collection = (TGBrowserCollection) context.getAttribute(ATTRIBUTE_COLLECTION);
			
			TGBrowserManager tgBrowserManager = TGBrowserManager.getInstance(getContext());
			tgBrowserManager.closeSession();
			if( collection != null ) {
				tgBrowserManager.openSession(collection);
			}
		} catch (TGBrowserException e) {
			throw new TGActionException(e);
		}
	}
}
