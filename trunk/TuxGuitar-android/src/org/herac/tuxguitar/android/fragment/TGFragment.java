package org.herac.tuxguitar.android.fragment;

import android.app.Fragment;
import android.view.ViewGroup;

public abstract class TGFragment extends Fragment {
	
	public TGFragment() {
		super();
	}

	public void onCreateDrawer(ViewGroup drawerView) {
		if( drawerView.getChildCount() > 0 ) {
			drawerView.removeAllViews();
		}
	}
}
