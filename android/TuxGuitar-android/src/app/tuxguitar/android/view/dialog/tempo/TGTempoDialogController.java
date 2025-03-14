package app.tuxguitar.android.view.dialog.tempo;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTempoDialogController extends TGModalFragmentController<TGTempoDialog> {

	@Override
	public TGTempoDialog createNewInstance() {
		return new TGTempoDialog();
	}
}
