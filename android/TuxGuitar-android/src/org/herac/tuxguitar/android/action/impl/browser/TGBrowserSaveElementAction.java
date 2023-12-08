package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGContext;

import java.io.OutputStream;

public class TGBrowserSaveElementAction extends TGActionBase{
	
	public static final String NAME = "action.browser.save-element";

	public static final String ATTRIBUTE_ELEMENT = TGBrowserElement.class.getName();
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	public static final String ATTRIBUTE_FORMAT = TGWriteSongAction.ATTRIBUTE_FORMAT;
	public static final String ATTRIBUTE_FORMAT_CODE = TGWriteSongAction.ATTRIBUTE_FORMAT_CODE;

	public TGBrowserSaveElementAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		final TGBrowserSession session = context.getAttribute(ATTRIBUTE_SESSION);
		final TGBrowserElement element = context.getAttribute(ATTRIBUTE_ELEMENT);
		final TGFileFormat fileFormat = context.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT);

		session.getBrowser().getOutputStream(new TGBrowserActionCallBack<OutputStream>(this, context) {
			public void onActionSuccess(TGActionContext context, OutputStream stream) {
				try{
					try {
						context.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, stream);

						TGActionManager.getInstance(getContext()).execute(TGWriteSongAction.NAME, context);

						session.setCurrentElement(element);
						session.setCurrentFormat(fileFormat);
					} finally {
						stream.close();
					}
				} catch(Throwable throwable){
					throw new TGActionException(throwable);
				}
			}
		}, element);
	}
}
