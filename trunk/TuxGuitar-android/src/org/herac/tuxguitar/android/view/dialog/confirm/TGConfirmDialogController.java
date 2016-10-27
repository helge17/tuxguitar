package org.herac.tuxguitar.android.view.dialog.confirm;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.TGDialogUtil;

import android.app.Activity;

public class TGConfirmDialogController implements TGDialogController {
	
	public static final String ATTRIBUTE_MESSAGE = "message";
	public static final String ATTRIBUTE_RUNNABLE = "runnable";
	public static final String ATTRIBUTE_CANCEL_RUNNABLE = "cancelRunnable";
	
	public TGConfirmDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
        TGDialogUtil.showDialog(activity, new TGConfirmDialog(), context);
	}
}
