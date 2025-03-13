package org.herac.tuxguitar.android.view.dialog.timeSignature;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTimeSignatureDialogController extends TGModalFragmentController<TGTimeSignatureDialog> {

	@Override
	public TGTimeSignatureDialog createNewInstance() {
		return new TGTimeSignatureDialog();
	}
}
