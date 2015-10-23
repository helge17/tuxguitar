package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGCachedFragment;
import org.herac.tuxguitar.android.menu.options.TGChannelListMenu;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class TGChannelListFragment extends TGCachedFragment {
	
	public TGChannelListFragment() {
		super(R.layout.view_channel_list);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.attachInstance();
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_channel_list, menu);
		
		TGActivity activity = (TGActivity) getActivity();
		TGChannelListMenu.getInstance(this.findContext()).initialize(activity, menu);
	}
	
	public void attachInstance() {
		TGChannelListFragmentController.getInstance(this.findContext()).attachInstance(this);
	}
}
