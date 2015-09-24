package org.herac.tuxguitar.android.view.dialog.stroke;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGStrokeDialogController implements TGDialogController {

	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGStrokeDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
