package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGTrackTuningModelDialogController extends TGModalFragmentController<TGTrackTuningModelDialog> {

	public static final String ATTRIBUTE_HANDLER = TGTrackTuningModelHandler.class.getName();
	public static final String ATTRIBUTE_MODEL = TGTrackTuningModel.class.getName();

	@Override
	public TGTrackTuningModelDialog createNewInstance() {
		return new TGTrackTuningModelDialog();
	}
}
