package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.fragment.TGCachedFragmentController;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

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
