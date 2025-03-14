package app.tuxguitar.android.view.dialog.measure;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasureCleanDialogController extends TGModalFragmentController<TGMeasureCleanDialog> {

	@Override
	public TGMeasureCleanDialog createNewInstance() {
		return new TGMeasureCleanDialog();
	}
}
