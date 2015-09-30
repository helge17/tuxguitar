package org.herac.tuxguitar.android.fragment;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.options.TGBrowserMenu;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class TGBrowserFragment extends TGCachedFragment {

	public TGBrowserFragment(TGContext context) {
		super(context, R.layout.view_browser);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_browser, menu);
		
		TGActivity activity = (TGActivity) getActivity();
		TGBrowserMenu.getInstance(this.findContext()).initialize(activity, menu);
	}
	
	public static TGBrowserFragment getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserFragment.class.getName(), new TGSingletonFactory<TGBrowserFragment>() {
			public TGBrowserFragment createInstance(TGContext context) {
				return new TGBrowserFragment(context);
			}
		});
	}
}
