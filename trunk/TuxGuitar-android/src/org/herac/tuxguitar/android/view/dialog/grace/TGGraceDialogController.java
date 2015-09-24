package org.herac.tuxguitar.android.view.dialog.grace;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGGraceDialogController implements TGDialogController {

	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGGraceDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
