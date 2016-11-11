package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.action.TGActionAsyncProcess;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;

public abstract class TGBrowserActionCallBack<T> extends TGActionAsyncProcess implements TGBrowserCallBack<T> {

	public TGBrowserActionCallBack(TGActionBase action, TGActionContext actionContext) {
		super(action, actionContext);

		this.onStart();
	}

	public void handleError(Throwable throwable) {
		this.onError(throwable);
	}

	public void onSuccess(T successData) {
		try {
			this.onActionSuccess(this.getActionContext(), successData);
			this.onEnd();
		} catch(Throwable e) {
			this.handleError(e);
		}
	}

	public abstract void onActionSuccess(TGActionContext actionContext, T successData) throws TGActionException;
}
