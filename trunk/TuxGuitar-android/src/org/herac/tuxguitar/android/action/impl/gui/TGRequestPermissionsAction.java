package org.herac.tuxguitar.android.action.impl.gui;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGContext;

public class TGRequestPermissionsAction extends TGActionBase {

	public static final String NAME = "action.gui.request-permissions";

	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_PERMISSIONS = "permissions";
	public static final String ATTRIBUTE_REQUEST_CODE = "requestCode";

	public TGRequestPermissionsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		Activity activity = context.getAttribute(ATTRIBUTE_ACTIVITY);
		String[] permissions = context.getAttribute(ATTRIBUTE_PERMISSIONS);
		Integer requestCode = context.getAttribute(ATTRIBUTE_REQUEST_CODE);

		ActivityCompat.requestPermissions(activity, permissions, requestCode);
	}
}
