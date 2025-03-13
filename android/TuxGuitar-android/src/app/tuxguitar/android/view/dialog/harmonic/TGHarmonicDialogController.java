package app.tuxguitar.android.view.dialog.harmonic;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGHarmonicDialogController extends TGModalFragmentController<TGHarmonicDialog> {

	@Override
	public TGHarmonicDialog createNewInstance() {
		return new TGHarmonicDialog();
	}
}
