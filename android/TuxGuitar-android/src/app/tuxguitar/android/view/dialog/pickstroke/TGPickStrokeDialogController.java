package app.tuxguitar.android.view.dialog.pickstroke;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGPickStrokeDialogController extends TGModalFragmentController<TGPickStrokeDialog> {

	@Override
	public TGPickStrokeDialog createNewInstance() {
		return new TGPickStrokeDialog();
	}
}
