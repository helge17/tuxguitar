package app.tuxguitar.android.view.dialog.channel;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGChannelEditDialogController extends TGModalFragmentController<TGChannelEditDialog> {

	public TGChannelEditDialogController() {
		super();
	}

	@Override
	public TGChannelEditDialog createNewInstance() {
		return new TGChannelEditDialog();
	}
}
