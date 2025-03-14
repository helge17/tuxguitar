package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGBrowserRunnableAction extends TGActionBase {

	public static final String NAME = "action.util.runnable";

	public static final String ATTRIBUTE_RUNNABLE = Runnable.class.getName();

	public TGBrowserRunnableAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Runnable runnable = context.getAttribute(ATTRIBUTE_RUNNABLE);
		runnable.run();
	}
}