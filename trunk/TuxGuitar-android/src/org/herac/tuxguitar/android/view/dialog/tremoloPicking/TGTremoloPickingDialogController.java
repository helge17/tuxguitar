package org.herac.tuxguitar.android.view.dialog.tremoloPicking;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGTremoloPickingDialogController implements TGDialogController {

	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGTremoloPickingDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
