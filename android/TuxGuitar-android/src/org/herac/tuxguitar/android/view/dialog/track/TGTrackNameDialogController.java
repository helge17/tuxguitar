package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackNameDialogController extends TGModalFragmentController<TGTrackNameDialog> {

	@Override
	public TGTrackNameDialog createNewInstance() {
		return new TGTrackNameDialog();
	}
}
