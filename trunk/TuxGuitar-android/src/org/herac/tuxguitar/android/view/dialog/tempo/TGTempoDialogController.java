package org.herac.tuxguitar.android.view.dialog.tempo;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGTempoDialogController implements TGDialogController {

	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGTempoDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
