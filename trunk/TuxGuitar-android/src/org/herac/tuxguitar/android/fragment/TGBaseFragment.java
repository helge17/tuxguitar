package org.herac.tuxguitar.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;

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
		return this.findActivity().findContext();
	}

	public TGActivity findActivity() {
		return ((TGActivity) getActivity());
	}

	public boolean isReady() {
		return (this.getView() != null && this.isVisible());
	}

	public void fireEvent(String action) throws TGActionException{
		TGEventManager.getInstance(findContext()).fireEvent(new TGFragmentEvent(this, action));
	}

	public void createActionBar(boolean hasOptionsMenu, boolean showIcon, String title) {
		this.setHasOptionsMenu(hasOptionsMenu);

		this.getActivity().getActionBar().setDisplayUseLogoEnabled(showIcon);
		this.getActivity().getActionBar().setDisplayShowHomeEnabled(showIcon);
		this.getActivity().getActionBar().setDisplayShowTitleEnabled(title != null);

		if( showIcon) {
			this.getActivity().getActionBar().setLogo(R.drawable.ic_launcher);
			this.getActivity().getActionBar().setLogo(R.drawable.ic_launcher);
		}

		if( title != null) {
			this.getActivity().getActionBar().setTitle(title);
		}
	}

	public void createActionBar(boolean hasOptionsMenu, boolean showIcon, int titleId) {
		this.createActionBar(hasOptionsMenu, showIcon, this.getActivity().getString(titleId));
	}

	public void postWhenReady(final Runnable runnable) {
		new Thread(new Runnable() {
			public void run() {
				if(!TGBaseFragment.this.isReady()) {
					TGBaseFragment.this.postWhenReady(runnable);
				}
				else {
					TGBaseFragment.this.getView().post(runnable);
				}
			}
		}).start();
	}
}