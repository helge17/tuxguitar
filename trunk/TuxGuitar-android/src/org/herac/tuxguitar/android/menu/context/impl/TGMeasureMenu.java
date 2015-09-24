package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController;

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
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.initializeItem(menu, R.id.menu_measure_go_first_measure, this.createActionProcessor(TGGoFirstMeasureAction.NAME), true);
		this.initializeItem(menu, R.id.menu_measure_go_last_measure, this.createActionProcessor(TGGoLastMeasureAction.NAME), true);
		this.initializeItem(menu, R.id.menu_measure_go_previous_measure, this.createActionProcessor(TGGoPreviousMeasureAction.NAME), true);
		this.initializeItem(menu, R.id.menu_measure_go_next_measure, this.createActionProcessor(TGGoNextMeasureAction.NAME), true);
		this.initializeItem(menu, R.id.menu_measure_add, new TGMeasureAddDialogController(), !running);
		this.initializeItem(menu, R.id.menu_measure_clean, new TGMeasureCleanDialogController(), !running);
		this.initializeItem(menu, R.id.menu_measure_remove, new TGMeasureRemoveDialogController(), !running);
	}
}