package app.tuxguitar.android.fragment.impl;

import app.tuxguitar.android.fragment.TGCachedFragmentController;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
