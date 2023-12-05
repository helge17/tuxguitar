package org.herac.tuxguitar.android.view.dialog.stroke;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGStrokeDialogController extends TGModalFragmentController<TGStrokeDialog> {

	@Override
	public TGStrokeDialog createNewInstance() {
		return new TGStrokeDialog();
	}
}
