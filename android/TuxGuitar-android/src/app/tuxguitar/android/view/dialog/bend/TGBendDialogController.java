package app.tuxguitar.android.view.dialog.bend;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGBendDialogController extends TGModalFragmentController<TGBendDialog> {

	@Override
	public TGBendDialog createNewInstance() {
		return new TGBendDialog();
	}
}
