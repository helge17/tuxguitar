package org.herac.tuxguitar.android.fragment;

import org.herac.tuxguitar.util.TGContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TGCachedFragment extends TGFragment {
	
	private int layout;
	private View view;
	
	public TGCachedFragment(TGContext context, int layout) {
		super(context);
		
		this.layout = layout;
	}
	
	@Override
	public View onPostCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View createdView) {
		if( this.view == null ) {
			this.view = inflater.inflate(this.layout, container, false);
		}
		return this.view;
	}
	
	public View getView() {
		return this.view;
	}
}
