package app.tuxguitar.android.fragment.impl;

import app.tuxguitar.android.fragment.TGCachedFragmentController;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBrowserFragmentController extends TGCachedFragmentController<TGBrowserFragment> {

	public TGBrowserFragment createNewInstance() {
		return new TGBrowserFragment();
	}

	public static TGBrowserFragmentController getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserFragmentController.class.getName(), new TGSingletonFactory<TGBrowserFragmentController>() {
			public TGBrowserFragmentController createInstance(TGContext context) {
				return new TGBrowserFragmentController();
			}
		});
	}
}
