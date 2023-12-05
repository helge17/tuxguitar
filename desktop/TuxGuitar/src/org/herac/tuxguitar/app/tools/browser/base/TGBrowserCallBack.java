package org.herac.tuxguitar.app.tools.browser.base;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserCallBack<T> extends TGErrorHandler {
	
	void onSuccess(T data);
}
