package app.tuxguitar.android.view.dialog.clef;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGClefDialogController extends TGModalFragmentController<TGClefDialog> {

	@Override
	public TGClefDialog createNewInstance() {
		return new TGClefDialog();
	}
}
