package org.herac.tuxguitar.android.menu.context;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TGMenuCabCallBack implements ActionMode.Callback {

	private View selectableView;
	private TGMenuController controller;

	public TGMenuCabCallBack(TGMenuController controller, View selectableView) {
		this.controller = controller;
		this.selectableView = selectableView;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		this.controller.inflate(menu, actionMode.getMenuInflater());
		if( this.selectableView != null ) {
			this.selectableView.setActivated(true);
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		if( this.selectableView != null ) {
			this.selectableView.setActivated(false);
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		actionMode.finish();
		return false;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}
}
