package org.herac.tuxguitar.android.drawer;

import android.view.ViewGroup;

import org.herac.tuxguitar.android.fragment.TGFragmentController;

public interface TGDrawerViewBuilder {

    void onOpenFragment(TGFragmentController<?> controller, ViewGroup drawerView);
}
