package org.herac.tuxguitar.android.action.impl.gui;

import android.app.Activity;
import android.content.Intent;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGContext;

public class TGStartActivityForResultAction extends TGActionBase {

	public static final String NAME = "action.gui.start-activity-for-result";

	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_INTENT = Intent.class.getName();
	public static final String ATTRIBUTE_REQUEST_CODE = "requestCode";

	public TGStartActivityForResultAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		Activity activity = context.getAttribute(ATTRIBUTE_ACTIVITY);
		Intent intent = context.getAttribute(ATTRIBUTE_INTENT);
		Integer requestCode = context.getAttribute(ATTRIBUTE_REQUEST_CODE);

		activity.startActivityForResult(intent, requestCode);
	}
}
