package app.tuxguitar.android.fragment;

import androidx.fragment.app.Fragment;

public interface TGFragmentController<T extends Fragment> {

	T getFragment();
}
