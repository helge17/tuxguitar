package org.herac.tuxguitar.android.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

public class TGFragmentTransaction extends FragmentTransaction {

	private FragmentTransaction handle;
	private boolean forceAllowingStateLoss;

	public TGFragmentTransaction(FragmentTransaction handle, boolean forceAllowingStateLoss) {
		this.handle = handle;
		this.forceAllowingStateLoss = forceAllowingStateLoss;
	}

	public TGFragmentTransaction(FragmentManager fragmentManager, boolean forceAllowingStateLoss) {
		this(fragmentManager.beginTransaction(), forceAllowingStateLoss);
	}

	@Override
	@NonNull
	public FragmentTransaction add(@NonNull Fragment fragment, @Nullable String tag) {
		return handle.add(fragment, tag);
	}

	@Override
	@NonNull
	public FragmentTransaction add(int containerViewId, @NonNull Fragment fragment) {
		return handle.add(containerViewId, fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction add(int containerViewId, @NonNull Fragment fragment, @Nullable String tag) {
		return handle.add(containerViewId, fragment, tag);
	}

	@Override
	@NonNull
	public FragmentTransaction replace(int containerViewId, @NonNull Fragment fragment) {
		return handle.replace(containerViewId, fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction replace(int containerViewId, @NonNull Fragment fragment, @Nullable String tag) {
		return handle.replace(containerViewId, fragment, tag);
	}

	@Override
	@NonNull
	public FragmentTransaction remove(@NonNull Fragment fragment) {
		return handle.remove(fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction hide(@NonNull Fragment fragment) {
		return handle.hide(fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction show(@NonNull Fragment fragment) {
		return handle.show(fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction detach(@NonNull Fragment fragment) {
		return handle.detach(fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction attach(@NonNull Fragment fragment) {
		return handle.attach(fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction setPrimaryNavigationFragment(@Nullable Fragment fragment) {
		return handle.setPrimaryNavigationFragment(fragment);
	}

	@Override
	@NonNull
	public FragmentTransaction setMaxLifecycle(@NonNull Fragment fragment, @NonNull Lifecycle.State state) {
		return handle.setMaxLifecycle(fragment, state);
	}

	@Override
	public boolean isEmpty() {
		return handle.isEmpty();
	}

	@Override
	@NonNull
	public FragmentTransaction setCustomAnimations(int enter, int exit) {
		return handle.setCustomAnimations(enter, exit);
	}

	@Override
	@NonNull
	public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
		return handle.setCustomAnimations(enter, exit, popEnter, popExit);
	}

	@Override
	@NonNull
	public FragmentTransaction addSharedElement(@NonNull View sharedElement, @NonNull String name) {
		return handle.addSharedElement(sharedElement, name);
	}

	@Override
	@NonNull
	public FragmentTransaction setTransition(int transition) {
		return handle.setTransition(transition);
	}

	@Override
	@NonNull
	public FragmentTransaction setTransitionStyle(int styleRes) {
		return handle.setTransitionStyle(styleRes);
	}

	@Override
	@NonNull
	public FragmentTransaction addToBackStack(@Nullable String name) {
		return handle.addToBackStack(name);
	}

	@Override
	public boolean isAddToBackStackAllowed() {
		return handle.isAddToBackStackAllowed();
	}

	@Override
	@NonNull
	public FragmentTransaction disallowAddToBackStack() {
		return handle.disallowAddToBackStack();
	}

	@Override
	@NonNull
	public FragmentTransaction setBreadCrumbTitle(int res) {
		return handle.setBreadCrumbTitle(res);
	}

	@Override
	@NonNull
	public FragmentTransaction setBreadCrumbTitle(@Nullable CharSequence text) {
		return handle.setBreadCrumbTitle(text);
	}

	@Override
	@NonNull
	public FragmentTransaction setBreadCrumbShortTitle(int res) {
		return handle.setBreadCrumbShortTitle(res);
	}

	@Override
	@NonNull
	public FragmentTransaction setBreadCrumbShortTitle(@Nullable CharSequence text) {
		return handle.setBreadCrumbShortTitle(text);
	}

	@Override
	@NonNull
	public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) {
		return handle.setReorderingAllowed(reorderingAllowed);
	}

	@Override
	@NonNull
	@Deprecated
	public FragmentTransaction setAllowOptimization(boolean allowOptimization) {
		return handle.setAllowOptimization(allowOptimization);
	}

	@Override
	@NonNull
	public FragmentTransaction runOnCommit(@NonNull Runnable runnable) {
		return handle.runOnCommit(runnable);
	}

	@Override
	public int commit() {
		if( this.forceAllowingStateLoss ) {
			return this.commitAllowingStateLoss();
		}
		return this.handle.commit();
	}

	@Override
	public int commitAllowingStateLoss() {
		return this.handle.commitAllowingStateLoss();
	}

	@Override
	public void commitNow() {
		if( this.forceAllowingStateLoss ) {
			this.commitAllowingStateLoss();
		} else {
			this.handle.commitNow();
		}
	}

	@Override
	public void commitNowAllowingStateLoss() {
		this.handle.commitNowAllowingStateLoss();
	}
}
