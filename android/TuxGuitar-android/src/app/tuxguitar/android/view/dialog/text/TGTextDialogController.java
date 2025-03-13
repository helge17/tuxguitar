package app.tuxguitar.android.view.dialog.text;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTextDialogController extends TGModalFragmentController<TGTextDialog> {

	@Override
	public TGTextDialog createNewInstance() {
		return new TGTextDialog();
	}
}
