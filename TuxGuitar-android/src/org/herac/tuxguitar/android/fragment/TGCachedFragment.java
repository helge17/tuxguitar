package org.herac.tuxguitar.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TGCachedFragment extends TGFragment {
	
	private int layout;
	private View view;
	
	public TGCachedFragment(int layout) {
		this.layout = layout;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if( this.view == null ) {
			this.view = inflater.inflate(this.layout, container, false);
		}
		return this.view;
	}
}
