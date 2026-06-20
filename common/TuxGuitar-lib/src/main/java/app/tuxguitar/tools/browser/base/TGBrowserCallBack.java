package app.tuxguitar.tools.browser.base;

import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserCallBack<T> extends TGErrorHandler {

	void onSuccess(T data);
}
