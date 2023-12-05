package org.herac.tuxguitar.android.browser.model;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserCallBack<T> extends TGErrorHandler {
	
	void onSuccess(T data);
}
