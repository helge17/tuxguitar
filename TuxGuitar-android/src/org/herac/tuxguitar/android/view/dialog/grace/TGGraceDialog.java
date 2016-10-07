package org.herac.tuxguitar.android.view.dialog.grace;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class TGGraceDialog extends TGDialog {

	public TGGraceDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGNote note = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		final TGString string = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_grace_dialog, null);
		
		this.fillFret(view, note);
		this.fillDeadNoteOption(view, note);
		this.fillOnBeatOptions(view, note);
		this.fillDurations(view, note);
		this.fillDynamics(view, note);
		this.fillTransitions(view, note);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.grace_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, createGrace(view, songManager));
				dialog.dismiss();
			}
		});
		builder.setNeutralButton(R.string.global_button_clean, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, null);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.global_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
	
	public TGSelectableItem[] createFretValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 0; i <= 100; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillFret(View view, TGNote note) {
		int selection = 0;
		if( note != null ) {
			selection = (note.getEffect().isGrace() ? note.getEffect().getGrace().getFret() : note.getValue());
		}
		
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createFretValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) view.findViewById(R.id.grace_dlg_fret_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)));
	}
	
	public int findSelectedFret(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.grace_dlg_fret_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillDeadNoteOption(View view, TGNote note) {
		boolean selection = false;
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().isDead();
		}
		
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.grace_dlg_dead_note_option);
		checkBox.setChecked(selection);
	}
	
	public boolean findDeadNoteValue(View view) {
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.grace_dlg_dead_note_option);
		
		return checkBox.isChecked();
	}
	
	public void fillOnBeatOptions(View view, TGNote note) {
		boolean selection = false;
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().isOnBeat();
		}
		
		this.fillOnBeatOption(view, R.id.grace_dlg_position_before_beat, false, selection);
		this.fillOnBeatOption(view, R.id.grace_dlg_position_on_beat, true, selection);
	}

	public void fillOnBeatOption(View view, int id, boolean value, boolean selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Boolean.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public boolean findSelectedOnBeat(View view) {
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.grace_dlg_position_group);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Boolean)radioButton.getTag()).booleanValue();
			}
		}
		return false;
	}
	
	public void fillDurations(View view, TGNote note) {
		int selection = 1;
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().getDuration();
		}
		
		this.fillDuration(view, R.id.grace_dlg_duration_16, 1, selection);
		this.fillDuration(view, R.id.grace_dlg_duration_32, 2, selection);
		this.fillDuration(view, R.id.grace_dlg_duration_64, 3, selection);
	}

	public void fillDuration(View view, int id, int value, int selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration(View view) {
		return findSelectedOption((RadioGroup) view.findViewById(R.id.grace_dlg_duration_group), 1);
	}
	
	public void fillDynamics(View view, TGNote note) {
		int selection = TGVelocities.DEFAULT;
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().getDynamic();
		}
		
		this.fillDynamic(view, R.id.grace_dlg_dynamic_ppp, TGVelocities.PIANO_PIANISSIMO, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_pp, TGVelocities.PIANISSIMO, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_p, TGVelocities.PIANO, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_mp, TGVelocities.MEZZO_PIANO, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_mf, TGVelocities.MEZZO_FORTE, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_f, TGVelocities.FORTE, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_ff, TGVelocities.FORTISSIMO, selection);
		this.fillDynamic(view, R.id.grace_dlg_dynamic_fff, TGVelocities.FORTE_FORTISSIMO, selection);
	}

	public void fillDynamic(View view, int id, int value, int selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDynamic(View view) {
		return findSelectedOption((RadioGroup) view.findViewById(R.id.grace_dlg_dynamic_group), TGVelocities.DEFAULT);
	}
	
	public void fillTransitions(View view, TGNote note) {
		int selection = TGEffectGrace.TRANSITION_NONE;
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().getTransition();
		}
		
		this.fillTransition(view, R.id.grace_dlg_transition_none, TGEffectGrace.TRANSITION_NONE, selection);
		this.fillTransition(view, R.id.grace_dlg_transition_bend, TGEffectGrace.TRANSITION_BEND, selection);
		this.fillTransition(view, R.id.grace_dlg_transition_slide, TGEffectGrace.TRANSITION_SLIDE, selection);
		this.fillTransition(view, R.id.grace_dlg_transition_hammer, TGEffectGrace.TRANSITION_HAMMER, selection);
	}

	public void fillTransition(View view, int id, int value, int selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedTransition(View view) {
		return findSelectedOption((RadioGroup) view.findViewById(R.id.grace_dlg_transition_group), TGEffectGrace.TRANSITION_NONE);
	}
	
	public int findSelectedOption(RadioGroup radioGroup, int defaultValue) {
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return defaultValue;
	}
	
	public TGEffectGrace createGrace(View view, TGSongManager songManager){
		TGEffectGrace tgEffectGrace = songManager.getFactory().newEffectGrace();
		tgEffectGrace.setDead(this.findDeadNoteValue(view));
		tgEffectGrace.setOnBeat(this.findSelectedOnBeat(view));
		tgEffectGrace.setFret(this.findSelectedFret(view));
		tgEffectGrace.setDuration(this.findSelectedDuration(view));
		tgEffectGrace.setDynamic(this.findSelectedDynamic(view));
		tgEffectGrace.setTransition(this.findSelectedTransition(view));
		return tgEffectGrace;
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, TGString string, TGEffectGrace effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeGraceNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeGraceNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
