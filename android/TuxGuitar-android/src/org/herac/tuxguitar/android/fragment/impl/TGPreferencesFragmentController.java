package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.fragment.TGFragmentController;

public class TGPreferencesFragmentController implements TGFragmentController<TGPreferencesFragment> {

	@Override
	public TGPreferencesFragment getFragment() {
		return new TGPreferencesFragment();
	}
}
