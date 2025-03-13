package app.tuxguitar.android.view.dialog.keySignature;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGKeySignatureDialogController extends TGModalFragmentController<TGKeySignatureDialog> {

	@Override
	public TGKeySignatureDialog createNewInstance() {
		return new TGKeySignatureDialog();
	}
}
