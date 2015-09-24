package org.herac.tuxguitar.android.view.dialog.channel;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;

import android.app.Activity;
import android.app.DialogFragment;

public class TGChannelEditDialogController implements TGDialogController {

	public TGChannelEditDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        DialogFragment dialog = new TGChannelEditDialog(context);
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
