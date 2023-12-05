package org.herac.tuxguitar.android.fragment.impl;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.fragment.TGCachedFragment;
import org.herac.tuxguitar.android.menu.controller.impl.fragment.TGBrowserMenu;

public class TGBrowserFragment extends TGCachedFragment {

	public TGBrowserFragment() {
		super(R.layout.view_browser);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.attachInstance();
		this.createActionBar(true, false, null);
	}
	
	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		TGBrowserMenu.getInstance(this.findContext()).inflate(menu, menuInflater);
	}
	
	public void attachInstance() {
		TGBrowserFragmentController.getInstance(this.findContext()).attachInstance(this);
	}
}
