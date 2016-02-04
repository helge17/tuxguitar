package org.herac.tuxguitar.android.action.impl.browser;

import java.io.OutputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.TGBrowserSyncCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserSaveElementAction extends TGActionBase{
	
	public static final String NAME = "action.browser.save-element";
	
	public static final String ATTRIBUTE_FORMAT = TGFileFormat.class.getName();
	public static final String ATTRIBUTE_ELEMENT = TGBrowserElement.class.getName();
	public static final String ATTRIBUTE_SESSION = TGBrowserSession.class.getName();
	
	public TGBrowserSaveElementAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		try{
			TGBrowserSession session = (TGBrowserSession) context.getAttribute(ATTRIBUTE_SESSION);
			TGBrowserElement element = (TGBrowserElement) context.getAttribute(ATTRIBUTE_ELEMENT);
			TGFileFormat fileFormat = (TGFileFormat) context.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT);
			
			TGBrowserSyncCallBack<OutputStream> syncCallBack = new TGBrowserSyncCallBack<OutputStream>();
			session.getBrowser().getOutputStream(syncCallBack, element);
			syncCallBack.syncCallBack();
			
			OutputStream stream = syncCallBack.getSuccessData();
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
}
