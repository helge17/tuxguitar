package org.herac.tuxguitar.android.view.dialog.measure;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasureCleanDialogController extends TGModalFragmentController<TGMeasureCleanDialog> {

	@Override
	public TGMeasureCleanDialog createNewInstance() {
		return new TGMeasureCleanDialog();
	}
}
