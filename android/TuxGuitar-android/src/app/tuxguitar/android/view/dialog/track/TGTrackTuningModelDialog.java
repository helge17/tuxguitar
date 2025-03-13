package org.herac.tuxguitar.android.view.dialog.track;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.util.ArrayList;
import java.util.List;

public class TGTrackTuningModelDialog extends TGModalFragment {

	private static final int MAX_OCTAVES = 10;

	public TGTrackTuningModelDialog() {
		super(R.layout.view_track_tuning_model_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.track_tuning_dlg_title);
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

	@SuppressWarnings("unchecked")
	public void fillTuning() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableTunings());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_value_control);
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				fillPreview();
			}

			public void onNothingSelected(AdapterView<?> adapterView) {}
		});

		TGTrackTuningModel model = this.getAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL);
		if( model != null ) {
			spinner.setSelection(((ArrayAdapter<TGSelectableItem>)spinner.getAdapter()).getPosition(new TGSelectableItem(Integer.valueOf(model.getValue()), null)), false);
		}
	}

	public int findSelectedValue() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_value_control);

		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}

	public TGSelectableItem[] createSelectableTunings() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();

		int count = (MAX_OCTAVES * TGTrackTuningLabel.KEY_NAMES.length);
		for (int i = 0; i < count; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), TGTrackTuningLabel.valueOf(i, true)));
		}

		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);

		return builtItems;
	}

	public boolean handleSelection() {
		TGTrackTuningModel model = new TGTrackTuningModel();
		model.setValue(this.findSelectedValue());

		if( model.getValue() == null ) {
			showErrorMessage(this.findActivity().getString(R.string.track_tuning_dlg_empty_value_error));
			return false;
		}
		TGTrackTuningModelHandler handler = this.getAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER);
		if( handler != null ) {
			handler.handleSelection(model);
		}
		return true;
	}

	public void showErrorMessage(String message) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, this.findActivity().getString(R.string.track_tuning_dlg_error_title));
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}
}
