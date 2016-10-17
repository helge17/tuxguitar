package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController;
import org.herac.tuxguitar.player.base.MidiPlayer;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGMeasureMenu extends TGContextMenuBase {
	
	public TGMeasureMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_measure);
		inflater.inflate(R.menu.menu_measure, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		
		this.initializeItem(menu, R.id.menu_measure_add, new TGMeasureAddDialogController(), !running);
		this.initializeItem(menu, R.id.menu_measure_clean, new TGMeasureCleanDialogController(), !running);
		this.initializeItem(menu, R.id.menu_measure_remove, new TGMeasureRemoveDialogController(), !running);
	}
}