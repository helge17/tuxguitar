package app.tuxguitar.android.action.impl.gui;

import android.app.Activity;
import androidx.core.app.ActivityCompat;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.util.TGContext;

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
