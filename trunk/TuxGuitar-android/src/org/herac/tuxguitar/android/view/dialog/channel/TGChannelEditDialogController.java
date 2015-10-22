package org.herac.tuxguitar.android.view.dialog.channel;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.TGDialogUtil;

import android.app.Activity;

public class TGChannelEditDialogController implements TGDialogController {

	public TGChannelEditDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        TGDialogUtil.showDialog(activity, new TGChannelEditDialog(), context);
	}
}
