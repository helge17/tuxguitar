package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.model.TGBrowserElement;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.util.TGContext;

public class TGBrowserSaveNewElementAction extends TGActionBase{

	public static final String NAME = "action.browser.save-new-element";

	public static final String ATTRIBUTE_NAME = "elementName";
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();

	public TGBrowserSaveNewElementAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		String elementName = context.getAttribute(ATTRIBUTE_NAME);
		TGBrowserSession session = context.getAttribute(ATTRIBUTE_SESSION);

		session.getBrowser().createElement(new TGBrowserActionCallBack<TGBrowserElement>(this, context) {
			public void onActionSuccess(TGActionContext context, TGBrowserElement element) {
				try{
					if( element != null && element.isWritable() ) {
						context.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_ELEMENT, element);

						TGActionManager.getInstance(getContext()).execute(TGBrowserSaveElementAction.NAME, context);
					}
				} catch(Throwable throwable){
					throw new TGActionException(throwable);
				}
			}
		}, elementName);
	}
}
