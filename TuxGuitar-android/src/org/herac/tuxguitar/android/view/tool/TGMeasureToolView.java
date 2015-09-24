package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController;

import android.content.Context;
import android.util.AttributeSet;

public class TGMeasureToolView extends TGToolView {
	
	public TGMeasureToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_measure_go_first_measure).setOnClickListener(this.createActionListener(TGGoFirstMeasureAction.NAME));
		this.findViewById(R.id.menu_measure_go_last_measure).setOnClickListener(this.createActionListener(TGGoLastMeasureAction.NAME));
		this.findViewById(R.id.menu_measure_go_previous_measure).setOnClickListener(this.createActionListener(TGGoPreviousMeasureAction.NAME));
		this.findViewById(R.id.menu_measure_go_next_measure).setOnClickListener(this.createActionListener(TGGoNextMeasureAction.NAME));
		this.findViewById(R.id.menu_measure_add).setOnClickListener(this.createDialogActionListener(new TGMeasureAddDialogController()));
		this.findViewById(R.id.menu_measure_clean).setOnClickListener(this.createDialogActionListener(new TGMeasureCleanDialogController()));
		this.findViewById(R.id.menu_measure_remove).setOnClickListener(this.createDialogActionListener(new TGMeasureRemoveDialogController()));
	}
	
	public void updateItems() {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.updateItem(R.id.menu_measure_add, !running);
		this.updateItem(R.id.menu_measure_clean, !running);
		this.updateItem(R.id.menu_measure_remove, !running);
	}
}
