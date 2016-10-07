package org.herac.tuxguitar.android.view.dialog.harmonic;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class TGHarmonicDialog extends TGDialog {

	public TGHarmonicDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGNote note = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		final TGString string = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_harmonic_dialog, null);
		
		this.fillHarmonics(view, note);
		this.fillData(view, note);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.harmonic_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, createHarmonic(view, songManager));
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
	
	public boolean isNaturalHarmonicAvailable(TGNote note) {
		if( note != null ) {
			for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
				if((note.getValue() % 12) == (TGEffectHarmonic.NATURAL_FREQUENCIES[i][0] % 12) ){
					return true;
				}
			}
		}
		return false;
	}
	
	public int getCurrentType(TGNote note) {
		if( note != null && note.getEffect().isHarmonic() ) {
			return note.getEffect().getHarmonic().getType();
		}
		return (isNaturalHarmonicAvailable(note) ? TGEffectHarmonic.TYPE_NATURAL : TGEffectHarmonic.TYPE_ARTIFICIAL);
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
	
	public void fillData(View view, TGNote note) {
		int type = getCurrentType(note);
		int selection = -1;
		if( note != null && note.getEffect().isHarmonic() ) {
			selection = note.getEffect().getHarmonic().getData();
		}
		
		this.fillData(view, type, selection);
	}
	
	public void fillData(View view, int type, int selection) {
		TGSelectableItem[] selectableItems = createDataValues(type);
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, selectableItems);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) view.findViewById(R.id.harmonic_dlg_data_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setEnabled(selectableItems.length > 0);
		spinner.setVisibility(selectableItems.length > 0 ? View.VISIBLE : View.GONE);
		if( selectableItems.length > 0 ) {
			spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)));
		}
	}
	
	public int findSelectedData(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.harmonic_dlg_data_value);
		TGSelectableItem selectableItem = (TGSelectableItem) spinner.getSelectedItem();
		return (selectableItem != null ? ((Integer)selectableItem.getItem()).intValue() : 0);
	}
	
	public void fillHarmonics(View view, TGNote note) {
		int selection = getCurrentType(note);
		boolean nhAvailable = isNaturalHarmonicAvailable(note);
		
		this.fillHarmonic(view, R.id.harmonic_dlg_type_nh, TGEffectHarmonic.TYPE_NATURAL, selection, nhAvailable);
		this.fillHarmonic(view, R.id.harmonic_dlg_type_ah, TGEffectHarmonic.TYPE_ARTIFICIAL, selection, true);
		this.fillHarmonic(view, R.id.harmonic_dlg_type_th, TGEffectHarmonic.TYPE_TAPPED, selection, true);
		this.fillHarmonic(view, R.id.harmonic_dlg_type_ph, TGEffectHarmonic.TYPE_PINCH, selection, true);
		this.fillHarmonic(view, R.id.harmonic_dlg_type_sh, TGEffectHarmonic.TYPE_SEMI, selection, true);
	}

	public void fillHarmonic(final View view, final int id, final int value, int selection, boolean enabled) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
		radioButton.setEnabled(enabled);
		radioButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fillData(view, value, 0);
			}
		});
	}
	
	public int findSelectedHarmonic(View view) {
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.harmonic_dlg_type_group);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGEffectHarmonic.TYPE_NATURAL;
	}
	
	public TGEffectHarmonic createHarmonic(View view, TGSongManager songManager){
		TGEffectHarmonic tgEffectHarmonic = songManager.getFactory().newEffectHarmonic();
		tgEffectHarmonic.setType(findSelectedHarmonic(view));
		tgEffectHarmonic.setData(findSelectedData(view));
		return tgEffectHarmonic;
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, TGString string, TGEffectHarmonic effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeHarmonicNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeHarmonicNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
