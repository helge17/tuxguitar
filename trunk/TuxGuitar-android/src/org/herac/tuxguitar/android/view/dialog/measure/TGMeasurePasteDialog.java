package org.herac.tuxguitar.android.view.dialog.measure;

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
import org.herac.tuxguitar.editor.action.measure.TGPasteMeasureAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import java.util.ArrayList;
import java.util.List;

public class TGMeasurePasteDialog extends TGModalFragment {

	public TGMeasurePasteDialog() {
		super(R.layout.view_measure_paste_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.measure_paste_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGMeasurePasteDialog.this.processAction();
				TGMeasurePasteDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillCount();
		this.fillOptions();
	}
	
	public TGSelectableItem[] createCountValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 1; i <= 100; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillCount() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createCountValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.measure_paste_dlg_count_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(1), null)));
	}
	
	public int findSelectedCount() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.measure_paste_dlg_count_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillOptions() {
		TGMeasureHeader header = this.getHeader();
		this.fillOption(R.id.measure_paste_dlg_options_mode_replace, TGPasteMeasureAction.TRANSFER_TYPE_REPLACE, true);
		this.fillOption(R.id.measure_paste_dlg_options_mode_insert, TGPasteMeasureAction.TRANSFER_TYPE_INSERT, false);
	}

	public void fillOption(int id, Integer value, boolean selected) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(selected);
	}
	
	public int findSelectedMode() {
		RadioGroup optionsGroup = (RadioGroup) this.getView().findViewById(R.id.measure_paste_dlg_options_group);
		
		int radioButtonId = optionsGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) optionsGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return 1;
	}
	
	public void processAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGPasteMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_COUNT, this.findSelectedCount());
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_MODE, this.findSelectedMode());
		tgActionProcessor.process();
	}


	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}
}
