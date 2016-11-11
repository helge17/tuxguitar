package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

import java.util.List;

public class TGBrowserRefreshAction extends TGActionBase{
	
	public static final String NAME = "action.browser.refresh";
	
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserRefreshAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		final TGBrowserSession tgBrowserSession = context.getAttribute(ATTRIBUTE_SESSION);
		if( tgBrowserSession.getBrowser() != null ) {
			tgBrowserSession.getBrowser().listElements(new TGBrowserActionCallBack<List<TGBrowserElement>>(this, context) {
				public void onActionSuccess(TGActionContext context, List<TGBrowserElement> elements) {
					tgBrowserSession.setCurrentElements(elements);
				}
			});
		}
	}
}
