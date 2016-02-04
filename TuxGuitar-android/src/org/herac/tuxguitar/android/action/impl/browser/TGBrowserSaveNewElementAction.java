package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.TGBrowserSyncCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserSaveNewElementAction extends TGActionBase{
	
	public static final String NAME = "action.browser.save-new-element";
	
	public static final String ATTRIBUTE_NAME = "elementName";
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserSaveNewElementAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		try{
			String elementName = context.getAttribute(ATTRIBUTE_NAME);
			TGBrowserSession session = (TGBrowserSession) context.getAttribute(ATTRIBUTE_SESSION);
			
			TGBrowserSyncCallBack<TGBrowserElement> tgBrowserSyncCallBack = new TGBrowserSyncCallBack<TGBrowserElement>();
			session.getBrowser().createElement(tgBrowserSyncCallBack, elementName);
			tgBrowserSyncCallBack.syncCallBack();
			
			TGBrowserElement element = tgBrowserSyncCallBack.getSuccessData();
			if( element != null && element.isWritable() ) {
				context.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_ELEMENT, element);
				
				TGActionManager.getInstance(getContext()).execute(TGBrowserSaveElementAction.NAME, context);
			}
		} catch(Throwable throwable){
			throw new TGActionException(throwable);
		}
	}
}
