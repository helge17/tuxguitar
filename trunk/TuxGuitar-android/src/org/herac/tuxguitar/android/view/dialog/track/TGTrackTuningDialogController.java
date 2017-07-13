package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackTuningDialogController extends TGModalFragmentController<TGTrackTuningDialog> {

	@Override
	public TGTrackTuningDialog createNewInstance() {
		return new TGTrackTuningDialog();
	}
}
