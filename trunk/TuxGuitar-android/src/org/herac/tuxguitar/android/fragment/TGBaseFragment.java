package org.herac.tuxguitar.android.fragment;

import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TGBaseFragment extends Fragment {
	
	public TGBaseFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.onPostCreate(savedInstanceState);
		this.fireEvent(TGFragmentEvent.ACTION_CREATED);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		
		this.onPostCreateOptionsMenu(menu, menuInflater);
		this.fireEvent(TGFragmentEvent.ACTION_OPTIONS_MENU_CREATED);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createdView = super.onCreateView(inflater, container, savedInstanceState);
		View view = this.onPostCreateView(inflater, container, savedInstanceState, createdView);
		
		this.fireEvent(TGFragmentEvent.ACTION_VIEW_CREATED);
		
		return view;
	}

	public void onPostCreate(Bundle savedInstanceState) {
		// override me
	}
	
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		// override me
	}

	public View onPostCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View createdView) {
		return createdView;
	}
	
	public TGContext findContext() {
		return ((TGActivity) getActivity()).findContext();
	}
	
	public void fireEvent(String action) throws TGActionException{
		TGEventManager.getInstance(findContext()).fireEvent(new TGFragmentEvent(this, action));
	}
}