package app.tuxguitar.android.view.dialog.message;

import app.tuxguitar.android.view.dialog.fragment.TGDialogFragmentController;

public class TGMessageDialogController extends TGDialogFragmentController<TGMessageDialog> {

	public static final String ATTRIBUTE_TITLE = "title";
	public static final String ATTRIBUTE_MESSAGE = "message";

	public TGMessageDialogController() {
		super();
	}

	@Override
	public TGMessageDialog createNewInstance() {
		return new TGMessageDialog();
	}
}
