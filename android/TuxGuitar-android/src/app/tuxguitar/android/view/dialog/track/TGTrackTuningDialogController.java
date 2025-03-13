package app.tuxguitar.android.view.dialog.track;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackTuningDialogController extends TGModalFragmentController<TGTrackTuningDialog> {

	@Override
	public TGTrackTuningDialog createNewInstance() {
		return new TGTrackTuningDialog();
	}
}
