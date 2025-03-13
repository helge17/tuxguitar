package org.herac.tuxguitar.android.view.dialog.pickstroke;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGPickStrokeDialogController extends TGModalFragmentController<TGPickStrokeDialog> {

	@Override
	public TGPickStrokeDialog createNewInstance() {
		return new TGPickStrokeDialog();
	}
}
