package app.tuxguitar.android.view.dialog.trill;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrillDialogController extends TGModalFragmentController<TGTrillDialog> {

	@Override
	public TGTrillDialog createNewInstance() {
		return new TGTrillDialog();
	}
}
