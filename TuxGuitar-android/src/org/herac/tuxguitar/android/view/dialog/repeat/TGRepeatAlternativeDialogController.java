package org.herac.tuxguitar.android.view.dialog.repeat;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGRepeatAlternativeDialogController extends TGModalFragmentController<TGRepeatAlternativeDialog> {

	@Override
	public TGRepeatAlternativeDialog createNewInstance() {
		return new TGRepeatAlternativeDialog();
	}
}
