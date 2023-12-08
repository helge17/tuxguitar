package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackChannelDialogController extends TGModalFragmentController<TGTrackChannelDialog> {

	@Override
	public TGTrackChannelDialog createNewInstance() {
		return new TGTrackChannelDialog();
	}
}
