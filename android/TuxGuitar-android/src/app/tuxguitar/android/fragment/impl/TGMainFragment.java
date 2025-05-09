package app.tuxguitar.android.fragment.impl;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import app.tuxguitar.android.R;
import app.tuxguitar.android.fragment.TGCachedFragment;
import app.tuxguitar.android.menu.controller.impl.fragment.TGMainMenu;

public class TGMainFragment extends TGCachedFragment {

	public TGMainFragment() {
		super(R.layout.view_main);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.attachInstance();
		this.createActionBar(true, false, R.string.app_name);
	}

	@Override
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		TGMainMenu.getInstance(this.findContext()).inflate(menu, menuInflater);
	}

	public View findChildViewById(int id) {
		if( this.getView() != null ) {
			return this.getView().findViewById(id);
		}
		return null;
	}

	public View getTopView() {
		return this.findChildViewById(R.id.main_top);
	}

	public View getBottomView() {
		return this.findChildViewById(R.id.main_bottom);
	}

	public View getLeftView() {
		return this.findChildViewById(R.id.main_left);
	}

	public View getRightView() {
		return this.findChildViewById(R.id.main_right);
	}

	public View getBodyView() {
		return this.findChildViewById(R.id.main_body);
	}

	public void attachInstance() {
		TGMainFragmentController.getInstance(this.findContext()).attachInstance(this);
	}
}
