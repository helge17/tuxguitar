package app.tuxguitar.android.view.dialog.fragment;

import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.application.TGApplicationUtil;
import app.tuxguitar.android.view.dialog.TGDialogContext;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.util.TGContext;

public abstract class TGDialogFragmentController<T extends TGDialogFragment> implements TGDialogController {

	public abstract T createNewInstance();

	public void showDialog(TGActivity activity, TGDialogContext context) {
		TGDialogFragment dialog = this.createNewInstance();
		TGContext tgContext = TGApplicationUtil.findContext(activity);
		tgContext.setAttribute(dialog.getDialogContextKey(), context);

		dialog.show(activity.getSupportFragmentManager(), "NoticeDialogFragment");
	}
}
