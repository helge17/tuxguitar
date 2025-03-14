package app.tuxguitar.android.view.dialog.grace;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGGraceDialogController extends TGModalFragmentController<TGGraceDialog> {

	@Override
	public TGGraceDialog createNewInstance() {
		return new TGGraceDialog();
	}
}
