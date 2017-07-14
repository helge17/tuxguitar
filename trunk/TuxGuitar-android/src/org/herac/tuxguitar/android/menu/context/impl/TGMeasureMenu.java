package org.herac.tuxguitar.android.menu.context.impl;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController;
import org.herac.tuxguitar.player.base.MidiPlayer;

public class TGMeasureMenu extends TGMenuBase {
	
	public TGMeasureMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_measure, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(Menu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		
		this.initializeItem(menu, R.id.menu_measure_add, new TGMeasureAddDialogController(), !running);
		this.initializeItem(menu, R.id.menu_measure_clean, new TGMeasureCleanDialogController(), !running);
		this.initializeItem(menu, R.id.menu_measure_remove, new TGMeasureRemoveDialogController(), !running);
	}
}