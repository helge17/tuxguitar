package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.model.TGBrowserElement;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.util.TGContext;

import java.io.InputStream;

public class TGBrowserOpenElementAction extends TGActionBase{

	public static final String NAME = "action.browser.open-element";

	public static final String ATTRIBUTE_ELEMENT = TGBrowserElement.class.getName();
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	public static final String ATTRIBUTE_FORMAT_CODE = TGReadSongAction.ATTRIBUTE_FORMAT_CODE;

	public TGBrowserOpenElementAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		final TGBrowserSession session = context.getAttribute(ATTRIBUTE_SESSION);
		final TGBrowserElement element = context.getAttribute(ATTRIBUTE_ELEMENT);

		if( session.getBrowser() != null ) {
			session.getBrowser().getInputStream(new TGBrowserActionCallBack<InputStream>(this, context) {
				public void onActionSuccess(TGActionContext context, InputStream stream) {
					try {
						try {
							context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);

							TGActionManager.getInstance(getContext()).execute(TGReadSongAction.NAME, context);

							session.setCurrentElement(element);
							session.setCurrentFormat((TGFileFormat) context.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT));
						} finally {
							stream.close();
						}
					} catch (Throwable throwable) {
						throw new TGActionException(throwable);
					}
				}
			}, element);
		}
	}
}
