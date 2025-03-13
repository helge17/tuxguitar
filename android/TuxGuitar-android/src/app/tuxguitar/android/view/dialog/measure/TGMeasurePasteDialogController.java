package app.tuxguitar.android.view.dialog.measure;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasurePasteDialogController extends TGModalFragmentController<TGMeasurePasteDialog> {

	@Override
	public TGMeasurePasteDialog createNewInstance() {
		return new TGMeasurePasteDialog();
	}
}
