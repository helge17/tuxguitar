package app.tuxguitar.android.view.dialog.browser.collection;

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController;

public class TGBrowserCollectionsDialogController extends TGModalFragmentController<TGBrowserCollectionsDialog> {

	public TGBrowserCollectionsDialogController() {
		super();
	}

	@Override
	public TGBrowserCollectionsDialog createNewInstance() {
		return new TGBrowserCollectionsDialog();
	}
}
