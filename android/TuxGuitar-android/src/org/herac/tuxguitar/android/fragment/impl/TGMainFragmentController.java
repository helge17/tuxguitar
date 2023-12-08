package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.fragment.TGCachedFragmentController;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

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
