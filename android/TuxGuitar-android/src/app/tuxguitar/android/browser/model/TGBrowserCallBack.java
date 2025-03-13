package app.tuxguitar.android.browser.model;

import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserCallBack<T> extends TGErrorHandler {

	void onSuccess(T data);
}
