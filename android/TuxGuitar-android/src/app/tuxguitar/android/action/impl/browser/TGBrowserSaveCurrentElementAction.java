package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.model.TGBrowserElement;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.util.TGContext;

public class TGBrowserSaveCurrentElementAction extends TGActionBase{

	public static final String NAME = "action.browser.save-current-element";

	public static final String ATTRIBUTE_SESSION = TGBrowserSaveElementAction.ATTRIBUTE_SESSION;

	public TGBrowserSaveCurrentElementAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		TGBrowserSession session = context.getAttribute(ATTRIBUTE_SESSION);
		TGBrowserElement element = session.getCurrentElement();
		TGFileFormat fileFormat = session.getCurrentFormat();
		if( element != null && element.isWritable() && fileFormat != null ) {
			context.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_ELEMENT, element);
			context.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT, fileFormat);

			TGActionManager.getInstance(getContext()).execute(TGBrowserSaveElementAction.NAME, context);
		} else {
			TGActionManager.getInstance(getContext()).execute(TGBrowserPrepareForWriteAction.NAME, context);
		}
	}
}
