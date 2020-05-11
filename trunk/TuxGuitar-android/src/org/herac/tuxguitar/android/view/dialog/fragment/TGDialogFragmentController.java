package org.herac.tuxguitar.android.view.dialog.fragment;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGDialogFragmentController<T extends TGDialogFragment> implements TGDialogController {

	public abstract T createNewInstance();

	public void showDialog(TGActivity activity, TGDialogContext context) {
		TGDialogFragment dialog = this.createNewInstance();
		TGContext tgContext = TGApplicationUtil.findContext(activity);
		tgContext.setAttribute(dialog.getDialogContextKey(), context);

		dialog.show(activity.getSupportFragmentManager(), "NoticeDialogFragment");
	}
}
