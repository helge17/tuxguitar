package app.tuxguitar.android.view.dialog.stroke;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGStrokeDialogController extends TGModalFragmentController<TGStrokeDialog> {

	@Override
	public TGStrokeDialog createNewInstance() {
		return new TGStrokeDialog();
	}
}
