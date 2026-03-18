package app.tuxguitar.android.view.dialog.measure;

import android.annotation.SuppressLint;
import android.os.Bundle;

/**
 * Android dialog for copying measures with range selection.
 * Allows users to select a range of measures to copy to clipboard.
 * 
 * @modified 2026-02-11 Added "Copy to End" button feature
 * @author TuxGuitar Team
 */
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import app.tuxguitar.android.R;
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import app.tuxguitar.android.view.util.TGSelectableItem;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.List;

public class TGMeasureCopyDialog extends TGModalFragment {

	public TGMeasureCopyDialog() {
		super(R.layout.view_measure_copy_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.measure_copy_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGMeasureCopyDialog.this.processAction();
				TGMeasureCopyDialog.this.close();

				return true;
			}
		});
	}

	/**
	 * Initializes the dialog view after inflation.
	 * Sets up range spinners, checkboxes, and the Copy to End button.
	 * 
	 * @modified 2026-02-11 Added Copy to End button initialization
	 */
	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillRanges();

		CheckBox copyAllTracks = (CheckBox) this.getView().findViewById(R.id.measure_copy_dlg_options_all_tracks);
		copyAllTracks.setChecked(true);
		copyAllTracks.setEnabled(this.getSong().countTracks() > 1);

		// ========== FEATURE ADDED: Copy to End Button ==========
		// Added 2026-02-11: Button to automatically select all measures from current to end
		// When clicked, sets the "to" spinner to the last measure in the track
		Button copyToEndButton = (Button) this.getView().findViewById(R.id.measure_copy_dlg_copy_to_end_button);
		copyToEndButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Update the "to" spinner to the maximum measure count
				Spinner toSpinner = (Spinner) getView().findViewById(R.id.measure_copy_dlg_to_value);
				updateSpinnerSelection(toSpinner, getTrack().countMeasures());
			}
		});
		// ========== END FEATURE ==========
	}

	public TGSelectableItem[] createRangeValues(int minimum, int maximum) {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = minimum; i <= maximum; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}

	public void fillSpinner(Spinner spinner, int minimum, int maximum) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createRangeValues(minimum, maximum));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(arrayAdapter);
	}

	public void fillRanges() {
		final int minimum = 1;
		final int maximum = this.getTrack().countMeasures();
		final int selection = this.getMeasure().getNumber();

		final Spinner spinner1 = (Spinner) this.getView().findViewById(R.id.measure_copy_dlg_from_value);
		final Spinner spinner2 = (Spinner) this.getView().findViewById(R.id.measure_copy_dlg_to_value);

		this.fillSpinner(spinner1, minimum, maximum);
		this.fillSpinner(spinner2, minimum, maximum);

		this.updateSpinnerSelection(spinner1, selection);
		this.updateSpinnerSelection(spinner2, selection);

		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	validateSpinner1Selection(spinner1, spinner2, minimum);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	validateSpinner1Selection(spinner1, spinner2, minimum);
		    }
		});

		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	validateSpinner2Selection(spinner1, spinner2, maximum);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	validateSpinner2Selection(spinner1, spinner2, maximum);
		    }
		});
	}

	public void validateSpinner1Selection(Spinner spinner1, Spinner spinner2, int minimum) {
		int selection1 = findSelectedValue(spinner1);
		int selection2 = findSelectedValue(spinner2);

		if( selection1 < minimum ){
			this.updateSpinnerSelection(spinner1, minimum);
		}else if(selection1 > selection2){
			this.updateSpinnerSelection(spinner1, selection2);
		}
	}

	public void validateSpinner2Selection(Spinner spinner1, Spinner spinner2, int maximum) {
		int selection1 = findSelectedValue(spinner1);
		int selection2 = findSelectedValue(spinner2);

		if( selection2 < selection1){
			this.updateSpinnerSelection(spinner2, selection1);
		}else if(selection2 > maximum){
			this.updateSpinnerSelection(spinner2, selection1);
		}
	}

	public int findSelectedMeasure1() {
		return this.findSelectedValue((Spinner) this.getView().findViewById(R.id.measure_copy_dlg_from_value));
	}

	public int findSelectedMeasure2() {
		return this.findSelectedValue((Spinner) this.getView().findViewById(R.id.measure_copy_dlg_to_value));
	}

	public int findSelectedValue(Spinner spinner) {
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}

	public Boolean findAllTracksValue() {
		return Boolean.valueOf(((CheckBox) this.getView().findViewById(R.id.measure_copy_dlg_options_all_tracks)).isChecked());
	}

	@SuppressWarnings("unchecked")
	public void updateSpinnerSelection(Spinner spinner, int selection) {
		ArrayAdapter<TGSelectableItem> adapter = (ArrayAdapter<TGSelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)), false);
	}

	public void processAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGCopyMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.getTrack());
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, this.findSelectedMeasure1());
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, this.findSelectedMeasure2());
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, this.findAllTracksValue());
		tgActionProcessor.process();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGTrack  getTrack() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}

	public TGMeasure getMeasure() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}
}
