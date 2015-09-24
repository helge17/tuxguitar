package org.herac.tuxguitar.android.view.dialog.message;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGMessageDialogController implements TGDialogController {

	public static final String ATTRIBUTE_TITLE = "title";
	public static final String ATTRIBUTE_MESSAGE = "message";
	
	public TGMessageDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGMessageDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
