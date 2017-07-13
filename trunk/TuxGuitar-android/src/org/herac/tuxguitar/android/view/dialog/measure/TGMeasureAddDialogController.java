package org.herac.tuxguitar.android.view.dialog.measure;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasureAddDialogController extends TGModalFragmentController<TGMeasureAddDialog> {

	@Override
	public TGMeasureAddDialog createNewInstance() {
		return new TGMeasureAddDialog();
	}
}
