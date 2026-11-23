package app.tuxguitar.android.view.dialog.track;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import app.tuxguitar.android.R;
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import app.tuxguitar.android.view.util.TGSelectableItem;
import app.tuxguitar.util.TGMusicKeyUtils;

import java.util.ArrayList;
import java.util.List;

public class TGTrackTuningModelDialog extends TGModalFragment {

	public TGTrackTuningModelDialog() {
		super(R.layout.view_track_tuning_model_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		TGTrackTuningModel model = this.getAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL);
		this.createActionBar(true, false, model != null ? R.string.track_tuning_dlg_edit_string_title : R.string.track_tuning_dlg_add_string_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if( handleSelection()) {
					close();
				}
				return true;
			}
		});
	}

	@Override
	public void onPostInflateView() {
		this.fillTuning();
	}

	public void fillPreview() {
		EditText editText = (EditText) this.getView().findViewById(R.id.track_tuning_dlg_preview_control);
		editText.setText(TGTrackTuningLabel.valueOf(this.findSelectedValue()));
	}

	public void fillTuning() {
		ArrayAdapter<TGSelectableItem> valueAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableTunings());
		valueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<Integer> midiNoteAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createMidiNoteValues());
		midiNoteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final Spinner valueSpinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_value_control);
		final Spinner midiNoteSpinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_midi_note_control);
		valueSpinner.setAdapter(valueAdapter);
		midiNoteSpinner.setAdapter(midiNoteAdapter);

		// keep spinners in sync
		valueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if( midiNoteSpinner.getSelectedItemPosition() != i ) {
					midiNoteSpinner.setSelection(i, false);
				}
				fillPreview();
			}

			public void onNothingSelected(AdapterView<?> adapterView) {}
		});
		midiNoteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if( valueSpinner.getSelectedItemPosition() != i ) {
					valueSpinner.setSelection(i, false);
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {}
		});

		TGTrackTuningModel model = this.getAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL);
		int position = (getInitialValue(model) - TGMusicKeyUtils.MIN_MIDI_NOTE);
		valueSpinner.setSelection(position, false);
		midiNoteSpinner.setSelection(position, false);
	}

	private static int getInitialValue(TGTrackTuningModel model) {
		int value = (model != null ? model.getValue() : TGMusicKeyUtils.MIN_MIDI_NOTE);
		return Math.max(TGMusicKeyUtils.MIN_MIDI_NOTE, Math.min(TGMusicKeyUtils.MAX_MIDI_NOTE, value));
	}

	public int findSelectedValue() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_value_control);

		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}

	public TGSelectableItem[] createSelectableTunings() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();

		for (int value = TGMusicKeyUtils.MIN_MIDI_NOTE; value <= TGMusicKeyUtils.MAX_MIDI_NOTE; value++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(value), TGMusicKeyUtils.sharpNoteFullName(value)));
		}

		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);

		return builtItems;
	}

	public Integer[] createMidiNoteValues() {
		Integer[] values = new Integer[TGMusicKeyUtils.MAX_MIDI_NOTE - TGMusicKeyUtils.MIN_MIDI_NOTE + 1];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.valueOf(TGMusicKeyUtils.MIN_MIDI_NOTE + i);
		}
		return values;
	}

	public boolean handleSelection() {
		TGTrackTuningModel model = new TGTrackTuningModel();
		model.setValue(this.findSelectedValue());

		TGTrackTuningModelHandler handler = this.getAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER);
		if( handler != null ) {
			handler.handleSelection(model);
		}
		return true;
	}
}
