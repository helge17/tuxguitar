package app.tuxguitar.android.drawer;

import android.view.ViewGroup;

import app.tuxguitar.android.fragment.TGFragmentController;

public interface TGDrawerViewBuilder {

    void onOpenFragment(TGFragmentController<?> controller, ViewGroup drawerView);
}
