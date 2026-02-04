package app.tuxguitar.android.view.dialog.transport;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTransportModeDialogController extends TGModalFragmentController<TGTransportModeDialog> {

	@Override
	public TGTransportModeDialog createNewInstance() {
		return new TGTransportModeDialog();
	}
}
