package org.herac.tuxguitar.android.view.expandableList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface TGExpandableHandler {
	
	View getView(LayoutInflater layoutInflater, boolean isLast, View convertView, ViewGroup parent);
}
