package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.fragment.TGCachedFragmentController;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGChannelListFragmentController extends TGCachedFragmentController<TGChannelListFragment> {

	public TGChannelListFragment createNewInstance() {
		return new TGChannelListFragment();
	}
	
	public static TGChannelListFragmentController getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGChannelListFragmentController.class.getName(), new TGSingletonFactory<TGChannelListFragmentController>() {
			public TGChannelListFragmentController createInstance(TGContext context) {
				return new TGChannelListFragmentController();
			}
		});
	}
}
