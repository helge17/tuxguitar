package app.tuxguitar.android.fragment.impl;

import app.tuxguitar.android.fragment.TGCachedFragmentController;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainFragmentController extends TGCachedFragmentController<TGMainFragment> {

	public TGMainFragment createNewInstance() {
		return new TGMainFragment();
	}

	public static TGMainFragmentController getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainFragmentController.class.getName(), new TGSingletonFactory<TGMainFragmentController>() {
			public TGMainFragmentController createInstance(TGContext context) {
				return new TGMainFragmentController();
			}
		});
	}
}
