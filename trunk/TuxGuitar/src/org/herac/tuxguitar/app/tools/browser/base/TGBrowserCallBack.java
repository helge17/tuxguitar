package org.herac.tuxguitar.app.tools.browser.base;

public interface TGBrowserCallBack<T> {
	
	void onSuccess(T data);
	
	void handleError(Throwable throwable);
}
