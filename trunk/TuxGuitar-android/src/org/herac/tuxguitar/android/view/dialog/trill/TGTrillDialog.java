package org.herac.tuxguitar.android.view.dialog.trill;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

import java.util.ArrayList;
import java.util.List;

public class TGTrillDialog extends TGModalFragment {

	public TGTrillDialog() {
		super(R.layout.view_trill_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.trill_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTrillDialog.this.updateEffect();
				TGTrillDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.menu_modal_fragment_button_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTrillDialog.this.cleanEffect();
				TGTrillDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillFret();
		this.fillDurations();
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
		int selection = 0;

		TGNote note = this.getNote();
		if( note != null ) {
			selection = (note.getEffect().isTrill() ? note.getEffect().getTrill().getFret() : note.getValue());
		}
		
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createFretValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.trill_dlg_fret_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)));
	}
	
	public int findSelectedFret() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.trill_dlg_fret_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillDurations() {
		int duration = TGDuration.EIGHTH;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isTrill() ){
			duration = note.getEffect().getTrill().getDuration().getValue();
		}
		
		this.fillDuration(R.id.trill_dlg_duration_16, TGDuration.SIXTEENTH, duration);
		this.fillDuration(R.id.trill_dlg_duration_32, TGDuration.THIRTY_SECOND, duration);
		this.fillDuration(R.id.trill_dlg_duration_64, TGDuration.SIXTY_FOURTH, duration);
	}

	public void fillDuration(int id, int value, int selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration() {
		RadioGroup optionsGroup = (RadioGroup) this.getView().findViewById(R.id.trill_dlg_duration_group);
		
		int radioButtonId = optionsGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) optionsGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGDuration.EIGHTH;
	}
	
	public TGEffectTrill createTrill(){
		TGEffectTrill tgEffectTrill = getSongManager().getFactory().newEffectTrill();
		tgEffectTrill.setFret(findSelectedFret());
		tgEffectTrill.getDuration().setValue(findSelectedDuration());
		return tgEffectTrill;
	}

	public void cleanEffect() {
		this.updateEffect(null);
	}

	public void updateEffect() {
		this.updateEffect(this.createTrill());
	}

	public void updateEffect(TGEffectTrill effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTrillNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, this.getString());
		tgActionProcessor.setAttribute(TGChangeTrillNoteAction.ATTRIBUTE_EFFECT, effect);
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
