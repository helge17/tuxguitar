package org.herac.tuxguitar.android.view.dialog.tremoloBar;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTremoloBarDialogController extends TGModalFragmentController<TGTremoloBarDialog> {

	@Override
	public TGTremoloBarDialog createNewInstance() {
		return new TGTremoloBarDialog();
	}
}
