package org.herac.tuxguitar.android.view.dialog.measure;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGMeasurePasteDialogController extends TGModalFragmentController<TGMeasurePasteDialog> {

	@Override
	public TGMeasurePasteDialog createNewInstance() {
		return new TGMeasurePasteDialog();
	}
}
