package org.herac.tuxguitar.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TGCachedFragment extends TGBaseFragment {
	
	private int layout;
	private View view;

	public TGCachedFragment(int layout) {
		this.layout = layout;
	}

	public View getView() {
		return this.view;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		this.onShowView();

		return view;
	}

	@Override
	public void onDestroyView() {
		this.onHideView();

		super.onDestroyView();
	}

	@Override
	public View onPostCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View createdView) {
		if( this.view == null ) {
			this.view = inflater.inflate(this.layout, container, false);
			this.onPostInflateView();
		}
		return this.view;
	}

	public void onPostInflateView() {
		// override me
	}

	public void onShowView() {
		// override me
	}

	public void onHideView() {
		// override me
	}
}
