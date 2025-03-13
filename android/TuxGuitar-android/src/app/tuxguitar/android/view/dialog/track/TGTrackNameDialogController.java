package app.tuxguitar.android.view.dialog.track;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackNameDialogController extends TGModalFragmentController<TGTrackNameDialog> {

	@Override
	public TGTrackNameDialog createNewInstance() {
		return new TGTrackNameDialog();
	}
}
