package org.herac.tuxguitar.android.view.dialog.tempo;

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
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTempo;

public class TGTempoDialog extends TGModalFragment {

	public TGTempoDialog() {
		super(R.layout.view_tempo_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.tempo_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTempoDialog.this.changeTempo();
				TGTempoDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		TGTempo tempo = this.getHeader().getTempo();
		ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createTempoValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.tempo_dlg_tempo_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(Integer.valueOf(tempo.getValue())));

		int applyToDefault = TGChangeTempoRangeAction.APPLY_TO_ALL;
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.tempo_dlg_options_apply_to_song), TGChangeTempoRangeAction.APPLY_TO_ALL, applyToDefault);
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.tempo_dlg_options_apply_to_end), TGChangeTempoRangeAction.APPLY_TO_END, applyToDefault);
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.tempo_dlg_options_apply_to_next_marker), TGChangeTempoRangeAction.APPLY_TO_NEXT, applyToDefault);
	}
	
	public Integer[] createTempoValues() {
		int length = ((TGChangeTempoRangeAction.MAX_TEMPO - TGChangeTempoRangeAction.MIN_TEMPO) + 1);
		Integer[] items = new Integer[length];
		for (int i = 0; i < length; i++) {
			items[i] = Integer.valueOf(i + TGChangeTempoRangeAction.MIN_TEMPO);
		}
		return items;
	}

	public void updateRadio(RadioButton button, Integer value, Integer selection) {
		button.setTag(Integer.valueOf(value));
		button.setChecked(selection != null && selection.equals(value));
	}

	public int parseTempoValue() {
		final Spinner spinner = (Spinner) this.getView().findViewById(R.id.tempo_dlg_tempo_value);

		return ((Integer) spinner.getSelectedItem()).intValue();
	}

	public Integer parseApplyTo() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.tempo_dlg_options_group);

		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGChangeTempoRangeAction.APPLY_TO_ALL;
	}

	public void changeTempo() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTempoRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.getHeader());
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, this.parseTempoValue());
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, this.parseApplyTo());
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}
}
