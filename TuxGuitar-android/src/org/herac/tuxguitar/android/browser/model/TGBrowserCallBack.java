package org.herac.tuxguitar.android.browser.model;

public interface TGBrowserCallBack<T> {
	
	void onSuccess(T data);
	
	void handleError(Throwable throwable);
}
