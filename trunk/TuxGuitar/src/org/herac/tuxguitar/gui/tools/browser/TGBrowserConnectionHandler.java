package org.herac.tuxguitar.gui.tools.browser;

import java.io.InputStream;
import java.util.List;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public interface TGBrowserConnectionHandler {
	
	public void notifyLockStatusChanged();
	
	public void notifyOpened(int callId);
	
	public void notifyClosed(int callId);
	
	public void notifyCd(int callId);
	
	public void notifyElements(int callId,List elements);
	
	public void notifyStream(int callId,InputStream stream,TGBrowserElement element);
	
	public void notifyError(int callId,Throwable throwable);
}
