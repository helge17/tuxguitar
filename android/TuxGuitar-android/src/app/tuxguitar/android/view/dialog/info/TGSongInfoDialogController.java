package app.tuxguitar.android.view.dialog.info;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGSongInfoDialogController extends TGModalFragmentController<TGSongInfoDialog> {

	@Override
	public TGSongInfoDialog createNewInstance() {
		return new TGSongInfoDialog();
	}
}
