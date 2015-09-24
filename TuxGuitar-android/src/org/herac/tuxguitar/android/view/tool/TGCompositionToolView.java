package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.view.dialog.clef.TGClefDialogController;
import org.herac.tuxguitar.android.view.dialog.info.TGSongInfoDialogController;
import org.herac.tuxguitar.android.view.dialog.keySignature.TGKeySignatureDialogController;
import org.herac.tuxguitar.android.view.dialog.repeat.TGRepeatAlternativeDialogController;
import org.herac.tuxguitar.android.view.dialog.repeat.TGRepeatCloseDialogController;
import org.herac.tuxguitar.android.view.dialog.tempo.TGTempoDialogController;
import org.herac.tuxguitar.android.view.dialog.timeSignature.TGTimeSignatureDialogController;
import org.herac.tuxguitar.android.view.dialog.tripletFeel.TGTripletFeelDialogController;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;

import android.content.Context;
import android.util.AttributeSet;

public class TGCompositionToolView extends TGToolView {
	
	public TGCompositionToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_composition_change_tempo).setOnClickListener(this.createDialogActionListener(new TGTempoDialogController()));
		this.findViewById(R.id.menu_composition_change_clef).setOnClickListener(this.createDialogActionListener(new TGClefDialogController()));
		this.findViewById(R.id.menu_composition_change_key_signature).setOnClickListener(this.createDialogActionListener(new TGKeySignatureDialogController()));
		this.findViewById(R.id.menu_composition_change_time_signature).setOnClickListener(this.createDialogActionListener(new TGTimeSignatureDialogController()));
		this.findViewById(R.id.menu_composition_change_triplet_feel).setOnClickListener(this.createDialogActionListener(new TGTripletFeelDialogController()));
		this.findViewById(R.id.menu_composition_change_info).setOnClickListener(this.createDialogActionListener(new TGSongInfoDialogController()));
		this.findViewById(R.id.menu_composition_repeat_alternative).setOnClickListener(this.createDialogActionListener(new TGRepeatAlternativeDialogController()));
		this.findViewById(R.id.menu_composition_repeat_close).setOnClickListener(this.createDialogActionListener(new TGRepeatCloseDialogController()));
		this.findViewById(R.id.menu_composition_repeat_open).setOnClickListener(this.createActionListener(TGRepeatOpenAction.NAME));
	}
	
	public void updateItems() {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.updateItem(R.id.menu_composition_change_tempo, !running);
		this.updateItem(R.id.menu_composition_change_clef, !running);
		this.updateItem(R.id.menu_composition_change_key_signature, !running);
		this.updateItem(R.id.menu_composition_change_time_signature, !running);
		this.updateItem(R.id.menu_composition_change_triplet_feel, !running);
		this.updateItem(R.id.menu_composition_change_info, !running);
		this.updateItem(R.id.menu_composition_repeat_alternative, !running);
		this.updateItem(R.id.menu_composition_repeat_close, !running);
		this.updateItem(R.id.menu_composition_repeat_open, !running);
	}
}
