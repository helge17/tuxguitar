package org.herac.tuxguitar.android.fragment;

import android.app.Fragment;

public interface TGFragmentController<T extends Fragment> {
	
	T getFragment();
}
