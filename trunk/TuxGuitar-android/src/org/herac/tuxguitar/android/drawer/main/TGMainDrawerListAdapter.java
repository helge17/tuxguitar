package org.herac.tuxguitar.android.drawer.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class TGMainDrawerListAdapter extends BaseAdapter {

	private TGMainDrawer mainDrawer;
	
	public TGMainDrawerListAdapter(TGMainDrawer mainDrawer) {
		this.mainDrawer = mainDrawer;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public LayoutInflater getLayoutInflater() {
		return (LayoutInflater) this.mainDrawer.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public TGMainDrawer getMainDrawer() {
		return mainDrawer;
	}
	
	public void attachListeners() {
		// Override me
	}
	
	public void detachListeners() {
		// Override me
	}
}
