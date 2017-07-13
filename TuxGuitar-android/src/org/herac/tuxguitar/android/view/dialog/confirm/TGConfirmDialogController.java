package org.herac.tuxguitar.android.view.dialog.confirm;

import org.herac.tuxguitar.android.view.dialog.fragment.TGDialogFragmentController;

public class TGConfirmDialogController extends TGDialogFragmentController<TGConfirmDialog> {
	
	public static final String ATTRIBUTE_MESSAGE = "message";
	public static final String ATTRIBUTE_RUNNABLE = "runnable";
	public static final String ATTRIBUTE_CANCEL_RUNNABLE = "cancelRunnable";
	
	public TGConfirmDialogController() {
		super();
	}

	@Override
	public TGConfirmDialog createNewInstance() {
		return new TGConfirmDialog();
	}
}
