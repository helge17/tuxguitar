package org.herac.tuxguitar.android.view.dialog.tremoloBar;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGTremoloBarDialogController implements TGDialogController {

	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGTremoloBarDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
