package org.herac.tuxguitar.android.view.dialog.tempo;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTempoDialogController extends TGModalFragmentController<TGTempoDialog> {

	@Override
	public TGTempoDialog createNewInstance() {
		return new TGTempoDialog();
	}
}
