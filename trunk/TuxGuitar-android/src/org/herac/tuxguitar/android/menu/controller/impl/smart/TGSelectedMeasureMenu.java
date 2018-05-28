package org.herac.tuxguitar.android.menu.controller.impl.smart;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.clef.TGClefDialogController;
import org.herac.tuxguitar.android.view.dialog.keySignature.TGKeySignatureDialogController;
import org.herac.tuxguitar.android.view.dialog.repeat.TGRepeatAlternativeDialogController;
import org.herac.tuxguitar.android.view.dialog.repeat.TGRepeatCloseDialogController;
import org.herac.tuxguitar.android.view.dialog.tempo.TGTempoDialogController;
import org.herac.tuxguitar.android.view.dialog.timeSignature.TGTimeSignatureDialogController;
import org.herac.tuxguitar.android.view.dialog.tripletFeel.TGTripletFeelDialogController;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;

public class TGSelectedMeasureMenu extends TGMenuBase {

	public TGSelectedMeasureMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_selected_measure, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		this.initializeItem(menu, R.id.action_change_tempo, new TGTempoDialogController(), true);
		this.initializeItem(menu, R.id.action_change_clef, new TGClefDialogController(), true);
		this.initializeItem(menu, R.id.action_change_key_signature, new TGKeySignatureDialogController(), true);
		this.initializeItem(menu, R.id.action_change_time_signature, new TGTimeSignatureDialogController(), true);
		this.initializeItem(menu, R.id.action_change_triplet_feel, new TGTripletFeelDialogController(), true);
		this.initializeItem(menu, R.id.action_change_repeat_alternative, new TGRepeatAlternativeDialogController(), true);
		this.initializeItem(menu, R.id.action_change_repeat_close, new TGRepeatCloseDialogController(), true);
		this.initializeItem(menu, R.id.action_change_repeat_open, this.createActionProcessor(TGRepeatOpenAction.NAME), true);
	}
}
