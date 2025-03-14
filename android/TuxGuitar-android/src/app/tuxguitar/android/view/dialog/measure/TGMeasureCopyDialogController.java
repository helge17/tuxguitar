package app.tuxguitar.android.view.dialog.measure;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasureCopyDialogController extends TGModalFragmentController<TGMeasureCopyDialog> {

	@Override
	public TGMeasureCopyDialog createNewInstance() {
		return new TGMeasureCopyDialog();
	}
}
