package app.tuxguitar.android.view.dialog.timeSignature;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTimeSignatureDialogController extends TGModalFragmentController<TGTimeSignatureDialog> {

	@Override
	public TGTimeSignatureDialog createNewInstance() {
		return new TGTimeSignatureDialog();
	}
}
