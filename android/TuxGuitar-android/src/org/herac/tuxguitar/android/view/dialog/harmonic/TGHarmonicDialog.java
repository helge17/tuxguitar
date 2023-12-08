package org.herac.tuxguitar.android.view.dialog.harmonic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;

import java.util.ArrayList;
import java.util.List;

public class TGHarmonicDialog extends TGModalFragment {

	public TGHarmonicDialog() {
		super(R.layout.view_harmonic_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.harmonic_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGHarmonicDialog.this.updateEffect();
				TGHarmonicDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.action_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGHarmonicDialog.this.cleanEffect();
				TGHarmonicDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillHarmonics();
		this.fillData();
	}
	
	public boolean isNaturalHarmonicAvailable() {
		TGNote note = this.getNote();
		if( note != null ) {
			for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
				if((note.getValue() % 12) == (TGEffectHarmonic.NATURAL_FREQUENCIES[i][0] % 12) ){
					return true;
				}
			}
		}
		return false;
	}
	
	public int getCurrentType() {
		TGNote note = this.getNote();
		if( note != null && note.getEffect().isHarmonic() ) {
			return note.getEffect().getHarmonic().getType();
		}
		return (isNaturalHarmonicAvailable() ? TGEffectHarmonic.TYPE_NATURAL : TGEffectHarmonic.TYPE_ARTIFICIAL);
	}
	
	public String getTypeLabel(int type){
		if(type == TGEffectHarmonic.TYPE_NATURAL){
			return TGEffectHarmonic.KEY_NATURAL;
		}
		if(type == TGEffectHarmonic.TYPE_ARTIFICIAL){
			return TGEffectHarmonic.KEY_ARTIFICIAL;
		}
		if(type == TGEffectHarmonic.TYPE_TAPPED){
			return TGEffectHarmonic.KEY_TAPPED;
		}
		if(type == TGEffectHarmonic.TYPE_PINCH){
			return TGEffectHarmonic.KEY_PINCH;
		}
		if(type == TGEffectHarmonic.TYPE_SEMI){
			return TGEffectHarmonic.KEY_SEMI;
		}
		return new String();
	}
	
	public TGSelectableItem[] createDataValues(int type) {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		if(type != TGEffectHarmonic.TYPE_NATURAL){
			String label = getTypeLabel(type);
			for(int i = 0; i < TGEffectHarmonic.NATURAL_FREQUENCIES.length; i ++){
				selectableItems.add(new TGSelectableItem(Integer.valueOf(i), label + "(" + Integer.toString(TGEffectHarmonic.NATURAL_FREQUENCIES[i][0]) + ")"));
			}
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillData() {
		int type = getCurrentType();
		int selection = -1;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isHarmonic() ) {
			selection = note.getEffect().getHarmonic().getData();
		}
		
		this.fillData(type, selection);
	}
	
	public void fillData(int type, int selection) {
		TGSelectableItem[] selectableItems = createDataValues(type);
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, selectableItems);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.harmonic_dlg_data_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setEnabled(selectableItems.length > 0);
		spinner.setVisibility(selectableItems.length > 0 ? View.VISIBLE : View.GONE);
		if( selectableItems.length > 0 ) {
			spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)));
		}
	}
	
	public int findSelectedData() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.harmonic_dlg_data_value);
		TGSelectableItem selectableItem = (TGSelectableItem) spinner.getSelectedItem();
		return (selectableItem != null ? ((Integer)selectableItem.getItem()).intValue() : 0);
	}
	
	public void fillHarmonics() {
		int selection = getCurrentType();
		boolean nhAvailable = isNaturalHarmonicAvailable();
		
		this.fillHarmonic(R.id.harmonic_dlg_type_nh, TGEffectHarmonic.TYPE_NATURAL, selection, nhAvailable);
		this.fillHarmonic(R.id.harmonic_dlg_type_ah, TGEffectHarmonic.TYPE_ARTIFICIAL, selection, true);
		this.fillHarmonic(R.id.harmonic_dlg_type_th, TGEffectHarmonic.TYPE_TAPPED, selection, true);
		this.fillHarmonic(R.id.harmonic_dlg_type_ph, TGEffectHarmonic.TYPE_PINCH, selection, true);
		this.fillHarmonic(R.id.harmonic_dlg_type_sh, TGEffectHarmonic.TYPE_SEMI, selection, true);
	}

	public void fillHarmonic(final int id, final int value, int selection, boolean enabled) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
		radioButton.setEnabled(enabled);
		radioButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fillData(value, 0);
			}
		});
	}
	
	public int findSelectedHarmonic() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.harmonic_dlg_type_group);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGEffectHarmonic.TYPE_NATURAL;
	}
	
	public TGEffectHarmonic createHarmonic(){
		TGEffectHarmonic tgEffectHarmonic = getSongManager().getFactory().newEffectHarmonic();
		tgEffectHarmonic.setType(findSelectedHarmonic());
		tgEffectHarmonic.setData(findSelectedData());
		return tgEffectHarmonic;
	}

	public void cleanEffect() {
		this.updateEffect(null);
	}

	public void updateEffect() {
		this.updateEffect(this.createHarmonic());
	}

	public void updateEffect(TGEffectHarmonic effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeHarmonicNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, this.getString());
		tgActionProcessor.setAttribute(TGChangeHarmonicNoteAction.ATTRIBUTE_EFFECT, effect);
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
