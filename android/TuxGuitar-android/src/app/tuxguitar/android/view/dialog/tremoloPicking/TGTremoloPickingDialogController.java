package app.tuxguitar.android.view.dialog.tremoloPicking;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTremoloPickingDialogController extends TGModalFragmentController<TGTremoloPickingDialog> {

	@Override
	public TGTremoloPickingDialog createNewInstance() {
		return new TGTremoloPickingDialog();
	}
}
