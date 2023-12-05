package org.herac.tuxguitar.android.view.dialog.keySignature;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGKeySignatureDialogController extends TGModalFragmentController<TGKeySignatureDialog> {

	@Override
	public TGKeySignatureDialog createNewInstance() {
		return new TGKeySignatureDialog();
	}
}
