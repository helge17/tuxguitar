package app.tuxguitar.android.view.dialog.tremoloBar;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTremoloBarDialogController extends TGModalFragmentController<TGTremoloBarDialog> {

	@Override
	public TGTremoloBarDialog createNewInstance() {
		return new TGTremoloBarDialog();
	}
}
