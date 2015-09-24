package org.herac.tuxguitar.android.drawer.menu;


import org.herac.tuxguitar.android.view.expandableList.TGExpandableHandler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TGExpandableItemHandler implements TGExpandableHandler {
	
	private int layout;
	
	public TGExpandableItemHandler(int layout) {
		this.layout = layout;
	}

	@Override
	public View getView(LayoutInflater layoutInflater, boolean isLast, View convertView, ViewGroup parent) {
		return layoutInflater.inflate(this.layout, parent, false);
	}
}
