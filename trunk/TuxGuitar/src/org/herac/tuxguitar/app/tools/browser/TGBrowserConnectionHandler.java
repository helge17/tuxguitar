package org.herac.tuxguitar.app.tools.browser;

import java.io.InputStream;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public interface TGBrowserConnectionHandler {
	
	public void notifyLockStatusChanged();
	
	public void notifyOpened(int callId);
	
	public void notifyClosed(int callId);
	
	public void notifyCd(int callId);
	
	public void notifyElements(int callId, List<TGBrowserElement> elements);
	
	public void notifyStream(int callId, InputStream stream, TGBrowserElement element);
	
	public void notifyError(int callId, Throwable throwable);
}
