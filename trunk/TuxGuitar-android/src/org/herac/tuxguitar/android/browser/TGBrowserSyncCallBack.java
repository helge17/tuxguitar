package org.herac.tuxguitar.android.browser;

import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;

public class TGBrowserSyncCallBack<T> implements TGBrowserCallBack<T> {
	
	private boolean finished;
	private T successData;
	private Throwable throwable;
	
	public TGBrowserSyncCallBack() {
		this.finished = false;
	}
	
	public void onSuccess(T successData) {
		this.successData = successData;
		this.finished = true;
	}

	public void handleError(Throwable throwable) {
		this.throwable = throwable;
		this.finished = true;
	}

	public T getSuccessData() {
		return this.successData;
	}
	
	public void syncCallBack() throws TGBrowserException {
		try {
			while(!this.finished) {
				Thread.yield();
			}
			
			if( this.throwable != null ) {
				throw this.throwable;
			}
		} catch (TGBrowserException e) {
			throw e;
		} catch (Throwable e) {
			throw new TGBrowserException(e);
		}
	}
}
