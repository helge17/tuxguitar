package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController;
import app.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController;
import app.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController;
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction;
import app.tuxguitar.player.base.MidiPlayer;

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

		this.initializeItem(menu, R.id.action_measure_add, new TGMeasureAddDialogController(), !running);
		this.initializeItem(menu, R.id.action_measure_clean, new TGMeasureCleanDialogController(), !running);
		this.initializeItem(menu, R.id.action_measure_remove, new TGMeasureRemoveDialogController(), !running);
		this.initializeItem(menu, R.id.action_remove_unused_voice, this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME), !running);
	}
}
