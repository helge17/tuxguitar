package org.herac.tuxguitar.android.view.dialog.grace;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
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

import java.util.ArrayList;
import java.util.List;

public class TGGraceDialog extends TGModalFragment {

	public TGGraceDialog() {
		super(R.layout.view_grace_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.grace_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGGraceDialog.this.updateEffect();
				TGGraceDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.action_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGGraceDialog.this.cleanEffect();
				TGGraceDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillFret();
		this.fillDeadNoteOption();
		this.fillOnBeatOptions();
		this.fillDurations();
		this.fillDynamics();
		this.fillTransitions();
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
	
	public void fillFret() {
		TGNote note = this.getNote();

		int selection = 0;
		if( note != null ) {
			selection = (note.getEffect().isGrace() ? note.getEffect().getGrace().getFret() : note.getValue());
		}
		
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createFretValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.grace_dlg_fret_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)));
	}
	
	public int findSelectedFret() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.grace_dlg_fret_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillDeadNoteOption() {
		boolean selection = false;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().isDead();
		}
		
		CheckBox checkBox = (CheckBox) this.getView().findViewById(R.id.grace_dlg_dead_note_option);
		checkBox.setChecked(selection);
	}
	
	public boolean findDeadNoteValue() {
		CheckBox checkBox = (CheckBox) this.getView().findViewById(R.id.grace_dlg_dead_note_option);
		
		return checkBox.isChecked();
	}
	
	public void fillOnBeatOptions() {
		boolean selection = false;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().isOnBeat();
		}
		
		this.fillOnBeatOption(R.id.grace_dlg_position_before_beat, false, selection);
		this.fillOnBeatOption(R.id.grace_dlg_position_on_beat, true, selection);
	}

	public void fillOnBeatOption(int id, boolean value, boolean selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Boolean.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public boolean findSelectedOnBeat() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.grace_dlg_position_group);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Boolean)radioButton.getTag()).booleanValue();
			}
		}
		return false;
	}
	
	public void fillDurations() {
		int selection = 1;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().getDuration();
		}
		
		this.fillDuration(R.id.grace_dlg_duration_16, 1, selection);
		this.fillDuration(R.id.grace_dlg_duration_32, 2, selection);
		this.fillDuration(R.id.grace_dlg_duration_64, 3, selection);
	}

	public void fillDuration(int id, int value, int selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration() {
		return findSelectedOption((RadioGroup) this.getView().findViewById(R.id.grace_dlg_duration_group), 1);
	}
	
	public void fillDynamics() {
		int selection = TGVelocities.DEFAULT;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().getDynamic();
		}
		
		this.fillDynamic(R.id.grace_dlg_dynamic_ppp, TGVelocities.PIANO_PIANISSIMO, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_pp, TGVelocities.PIANISSIMO, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_p, TGVelocities.PIANO, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_mp, TGVelocities.MEZZO_PIANO, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_mf, TGVelocities.MEZZO_FORTE, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_f, TGVelocities.FORTE, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_ff, TGVelocities.FORTISSIMO, selection);
		this.fillDynamic(R.id.grace_dlg_dynamic_fff, TGVelocities.FORTE_FORTISSIMO, selection);
	}

	public void fillDynamic(int id, int value, int selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDynamic() {
		return findSelectedOption((RadioGroup) this.getView().findViewById(R.id.grace_dlg_dynamic_group), TGVelocities.DEFAULT);
	}
	
	public void fillTransitions() {
		int selection = TGEffectGrace.TRANSITION_NONE;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isGrace() ) {
			selection = note.getEffect().getGrace().getTransition();
		}
		
		this.fillTransition(R.id.grace_dlg_transition_none, TGEffectGrace.TRANSITION_NONE, selection);
		this.fillTransition(R.id.grace_dlg_transition_bend, TGEffectGrace.TRANSITION_BEND, selection);
		this.fillTransition(R.id.grace_dlg_transition_slide, TGEffectGrace.TRANSITION_SLIDE, selection);
		this.fillTransition(R.id.grace_dlg_transition_hammer, TGEffectGrace.TRANSITION_HAMMER, selection);
	}

	public void fillTransition(int id, int value, int selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedTransition() {
		return findSelectedOption((RadioGroup) this.getView().findViewById(R.id.grace_dlg_transition_group), TGEffectGrace.TRANSITION_NONE);
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
	
	public TGEffectGrace createGrace(){
		TGEffectGrace tgEffectGrace = getSongManager().getFactory().newEffectGrace();
		tgEffectGrace.setDead(this.findDeadNoteValue());
		tgEffectGrace.setOnBeat(this.findSelectedOnBeat());
		tgEffectGrace.setFret(this.findSelectedFret());
		tgEffectGrace.setDuration(this.findSelectedDuration());
		tgEffectGrace.setDynamic(this.findSelectedDynamic());
		tgEffectGrace.setTransition(this.findSelectedTransition());
		return tgEffectGrace;
	}

	public void cleanEffect() {
		this.updateEffect(null);
	}

	public void updateEffect() {
		this.updateEffect(this.createGrace());
	}

	public void updateEffect(TGEffectGrace effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeGraceNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, this.getString());
		tgActionProcessor.setAttribute(TGChangeGraceNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}

	public TGSongManager getSongManager() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}

	public TGMeasure getMeasure() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}

	public TGBeat getBeat() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
	}

	public TGNote getNote() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
	}

	public TGString getString() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
	}
}
