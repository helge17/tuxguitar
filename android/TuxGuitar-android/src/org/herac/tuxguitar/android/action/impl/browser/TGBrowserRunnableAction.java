package org.herac.tuxguitar.android.action.impl.browser;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

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