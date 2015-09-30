package org.herac.tuxguitar.android.fragment;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.options.TGMainMenu;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;

public class TGMainFragment extends TGCachedFragment {
	
	public TGMainFragment(TGContext context) {
		super(context, R.layout.view_main);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_main, menu);
		
		TGActivity activity = (TGActivity) getActivity();
		TGMainMenu.getInstance(this.findContext()).initialize(activity, menu);
	}
	
	@Override
	public void onPostCreateDrawer(ViewGroup drawerView) {
		this.getActivity().getLayoutInflater().inflate(R.layout.view_main_drawer, drawerView);
	}
	
	public static TGMainFragment getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainFragment.class.getName(), new TGSingletonFactory<TGMainFragment>() {
			public TGMainFragment createInstance(TGContext context) {
				return new TGMainFragment(context);
			}
		});
	}
}
