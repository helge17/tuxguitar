package org.herac.tuxguitar.android.fragment.impl;

import org.herac.tuxguitar.android.fragment.TGFragmentController;

public class TGTransportPreferencesFragmentController implements TGFragmentController<TGTransportPreferencesFragment> {

	@Override
	public TGTransportPreferencesFragment getFragment() {
		return new TGTransportPreferencesFragment();
	}
}
