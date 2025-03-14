package app.tuxguitar.android.fragment.impl;

import app.tuxguitar.android.fragment.TGFragmentController;

public class TGPreferencesFragmentController implements TGFragmentController<TGPreferencesFragment> {

	@Override
	public TGPreferencesFragment getFragment() {
		return new TGPreferencesFragment();
	}
}
