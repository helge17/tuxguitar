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
	
	public TGMainFragment() {
		super(R.layout.view_main);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_main, menu);
		
		TGActivity activity = (TGActivity) getActivity();
		TGMainMenu.getInstance(activity.findContext()).initialize(activity, menu);
	}
	
	@Override
	public void onCreateDrawer(ViewGroup drawerView) {
		super.onCreateDrawer(drawerView);
		
		this.getActivity().getLayoutInflater().inflate(R.layout.view_main_drawer, drawerView);
	}
	
	public static TGMainFragment getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainFragment.class.getName(), new TGSingletonFactory<TGMainFragment>() {
			public TGMainFragment createInstance(TGContext context) {
				return new TGMainFragment();
			}
		});
	}
}
