package org.herac.tuxguitar.android.view.dialog.confirm;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGConfirmDialogController implements TGDialogController {
	
	public static final String ATTRIBUTE_MESSAGE = "message";
	public static final String ATTRIBUTE_RUNNABLE = "runnable";
	
	public TGConfirmDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGConfirmDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
