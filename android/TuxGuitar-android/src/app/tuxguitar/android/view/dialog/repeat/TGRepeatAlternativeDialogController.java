package app.tuxguitar.android.view.dialog.repeat;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGRepeatAlternativeDialogController extends TGModalFragmentController<TGRepeatAlternativeDialog> {

	@Override
	public TGRepeatAlternativeDialog createNewInstance() {
		return new TGRepeatAlternativeDialog();
	}
}
