package app.tuxguitar.android.view.dialog.measure;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasureRemoveDialogController extends TGModalFragmentController<TGMeasureRemoveDialog> {

	@Override
	public TGMeasureRemoveDialog createNewInstance() {
		return new TGMeasureRemoveDialog();
	}
}
