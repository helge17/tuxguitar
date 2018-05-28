package org.herac.tuxguitar.android.fragment.impl;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.fragment.TGCachedFragment;
import org.herac.tuxguitar.android.menu.controller.impl.fragment.TGChannelListMenu;

public class TGChannelListFragment extends TGCachedFragment {
	
	public TGChannelListFragment() {
		super(R.layout.view_channel_list);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.attachInstance();
		this.createActionBar(true, false, R.string.channel_list);
	}
	
	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		TGChannelListMenu.getInstance(this.findContext()).inflate(menu, menuInflater);
	}
	
	public void attachInstance() {
		TGChannelListFragmentController.getInstance(this.findContext()).attachInstance(this);
	}
}
