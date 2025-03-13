package org.herac.tuxguitar.android.view.dialog.tremoloPicking;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTremoloPickingDialogController extends TGModalFragmentController<TGTremoloPickingDialog> {

	@Override
	public TGTremoloPickingDialog createNewInstance() {
		return new TGTremoloPickingDialog();
	}
}
