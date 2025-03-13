package app.tuxguitar.android.fragment.impl;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.fragment.TGCachedFragment;
import app.tuxguitar.android.menu.controller.impl.fragment.TGChannelListMenu;

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
