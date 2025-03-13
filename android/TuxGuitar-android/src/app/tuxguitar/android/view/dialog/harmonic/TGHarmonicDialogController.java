package org.herac.tuxguitar.android.view.dialog.harmonic;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGHarmonicDialogController extends TGModalFragmentController<TGHarmonicDialog> {

	@Override
	public TGHarmonicDialog createNewInstance() {
		return new TGHarmonicDialog();
	}
}
