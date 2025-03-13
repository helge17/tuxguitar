package org.herac.tuxguitar.android.view.dialog.channel;

import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGChannelEditDialogController extends TGModalFragmentController<TGChannelEditDialog> {

	public TGChannelEditDialogController() {
		super();
	}

	@Override
	public TGChannelEditDialog createNewInstance() {
		return new TGChannelEditDialog();
	}
}
