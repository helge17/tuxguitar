package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionAsyncProcess;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.model.TGBrowserCallBack;

public abstract class TGBrowserActionCallBack<T> extends TGActionAsyncProcess implements TGBrowserCallBack<T> {

	public TGBrowserActionCallBack(TGActionBase action, TGActionContext actionContext) {
		super(action, actionContext);

		this.onStart();
	}

	public void onSuccess(T successData) {
		this.callRunnableActionInNewThread(successData);
	}

	public void callRunnableActionInNewThread(final T successData) {
		new Thread(new Runnable() {
			public void run() {
				callRunnableActionInCurrentThread(successData);
			}
		}).start();
	}

	public void callRunnableActionInCurrentThread(T successData) {
		try {
			this.getActionContext().setAttribute(TGBrowserRunnableAction.ATTRIBUTE_RUNNABLE, this.createOnActionSuccessRunnable(successData));

			TGActionManager tgActionManager = TGActionManager.getInstance(this.getAction().getContext());
			tgActionManager.execute(TGBrowserRunnableAction.NAME, this.getActionContext());

			this.onFinish();
		} catch (TGActionException e) {
			this.handleError(e);
		}
	}

	public Runnable createOnActionSuccessRunnable(final T successData) {
		return new Runnable() {
			public void run() {
				onActionSuccess(successData);
			}
		};
	}

	public void onActionSuccess(final T successData) {
		this.onActionSuccess(this.getActionContext(), successData);
	}

	public abstract void onActionSuccess(TGActionContext actionContext, T successData) throws TGActionException;
}
