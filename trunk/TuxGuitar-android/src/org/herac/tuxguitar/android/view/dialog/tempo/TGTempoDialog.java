package org.herac.tuxguitar.android.view.dialog.tempo;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTempo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class TGTempoDialog extends TGDialog {

	public TGTempoDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_tempo_dialog, null);
		
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final TGTempo tempo = header.getTempo();
		ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createTempoValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner spinner = (Spinner) view.findViewById(R.id.tempo_dlg_tempo_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(Integer.valueOf(tempo.getValue())));
		
		final RadioGroup applyToGroup = (RadioGroup) view.findViewById(R.id.tempo_dlg_options_group);
		
		final int applyToDefault = TGChangeTempoRangeAction.APPLY_TO_ALL;
		this.updateRadio((RadioButton)view.findViewById(R.id.tempo_dlg_options_apply_to_song), TGChangeTempoRangeAction.APPLY_TO_ALL, applyToDefault);
		this.updateRadio((RadioButton)view.findViewById(R.id.tempo_dlg_options_apply_to_end), TGChangeTempoRangeAction.APPLY_TO_END, applyToDefault);
		this.updateRadio((RadioButton)view.findViewById(R.id.tempo_dlg_options_apply_to_next_marker), TGChangeTempoRangeAction.APPLY_TO_NEXT, applyToDefault);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.tempo_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeTempo(song, header, parseTempoValue(spinner), parseApplyTo(applyToGroup, applyToDefault));
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
	
	public Integer[] createTempoValues() {
		int length = ((TGChangeTempoRangeAction.MAX_TEMPO - TGChangeTempoRangeAction.MIN_TEMPO) + 1);
		Integer[] items = new Integer[length];
		for (int i = 0; i < length; i++) {
			items[i] = Integer.valueOf(i + TGChangeTempoRangeAction.MIN_TEMPO);
		}
		return items;
	}
	
	public int parseTempoValue(Spinner tempo) {
		return ((Integer)tempo.getSelectedItem()).intValue();
	}
	
	public void updateRadio(RadioButton button, Integer value, Integer selection) {
		button.setTag(Integer.valueOf(value));
		button.setChecked(selection != null && selection.equals(value));
	}

	public Integer parseApplyTo(RadioGroup radioGroup, Integer defaultValue) {
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return defaultValue;
	}
	
	public void changeTempo(TGSong song, TGMeasureHeader header, Integer value, Integer applyTo) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTempoRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, value);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, applyTo);
		tgActionProcessor.processOnNewThread();
	}
}
