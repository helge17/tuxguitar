package org.herac.tuxguitar.android.view.dialog.grace;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGGraceDialogController extends TGModalFragmentController<TGGraceDialog> {

	@Override
	public TGGraceDialog createNewInstance() {
		return new TGGraceDialog();
	}
}
