package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGCachedFragment;
import org.herac.tuxguitar.android.menu.options.TGBrowserMenu;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class TGBrowserFragment extends TGCachedFragment {

	public TGBrowserFragment() {
		super(R.layout.view_browser);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.attachInstance();
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_browser, menu);
		
		TGActivity activity = (TGActivity) getActivity();
		TGBrowserMenu.getInstance(this.findContext()).initialize(activity, menu);
	}
	
	public void attachInstance() {
		TGBrowserFragmentController.getInstance(this.findContext()).attachInstance(this);
	}
}
