package org.herac.tuxguitar.android.browser;

import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;

public class TGBrowserEmptyCallBack<T> implements TGBrowserCallBack<T> {
	
	public TGBrowserEmptyCallBack() {
		super();
	}
	
	public void onSuccess(T successData) {
		// nothing to do
	}

	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
	}
}
