package org.herac.tuxguitar.android.view.dialog.repeat;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGRepeatCloseDialogController extends TGModalFragmentController<TGRepeatCloseDialog> {

	@Override
	public TGRepeatCloseDialog createNewInstance() {
		return new TGRepeatCloseDialog();
	}
}
