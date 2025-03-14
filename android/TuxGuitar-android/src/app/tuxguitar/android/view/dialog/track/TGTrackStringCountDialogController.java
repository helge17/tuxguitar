package app.tuxguitar.android.view.dialog.track;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackStringCountDialogController extends TGModalFragmentController<TGTrackStringCountDialog> {

	@Override
	public TGTrackStringCountDialog createNewInstance() {
		return new TGTrackStringCountDialog();
	}
}
