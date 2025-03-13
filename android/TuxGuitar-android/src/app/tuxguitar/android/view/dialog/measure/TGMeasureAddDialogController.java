package app.tuxguitar.android.view.dialog.measure;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasureAddDialogController extends TGModalFragmentController<TGMeasureAddDialog> {

	@Override
	public TGMeasureAddDialog createNewInstance() {
		return new TGMeasureAddDialog();
	}
}
