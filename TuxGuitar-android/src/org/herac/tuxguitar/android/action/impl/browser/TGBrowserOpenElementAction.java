package org.herac.tuxguitar.android.action.impl.browser;

import java.io.InputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.TGBrowserSyncCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserOpenElementAction extends TGActionBase{
	
	public static final String NAME = "action.browser.open-element";
	
	public static final String ATTRIBUTE_ELEMENT = TGBrowserElement.class.getName();
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserOpenElementAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		try{
			TGBrowserSession session = (TGBrowserSession) context.getAttribute(ATTRIBUTE_SESSION);
			TGBrowserElement element = (TGBrowserElement) context.getAttribute(ATTRIBUTE_ELEMENT);
			
			TGBrowserSyncCallBack<InputStream> syncCallBack = new TGBrowserSyncCallBack<InputStream>();
			session.getBrowser().getInputStream(syncCallBack, element);
			syncCallBack.syncCallBack();
			
			InputStream stream = syncCallBack.getSuccessData();
			try {
				context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);
				
				TGActionManager.getInstance(getContext()).execute(TGReadSongAction.NAME, context);
				
				session.setCurrentElement(element);
				session.setCurrentFormat((TGFileFormat) context.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT));
				
			} finally {
				stream.close();
			}
		} catch(Throwable throwable){
			throw new TGActionException(throwable);
		}
	}
}
