package org.herac.tuxguitar.android.view.dialog.measure;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class TGMeasureAddDialog extends TGDialog {

	public TGMeasureAddDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_measure_add_dialog, null);
		
		this.fillCount(view);
		this.fillOptions(view, song, header);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.measure_add_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(song, findSelectedMeasureNumber(view), findSelectedCount(view));
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
	
	public TGSelectableItem[] createCountValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 1; i <= 100; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillCount(View view) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createCountValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) view.findViewById(R.id.measure_add_dlg_count_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(1), null)));
	}
	
	public int findSelectedCount(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.measure_add_dlg_count_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillOptions(View view, TGSong song, TGMeasureHeader header) {
		this.fillOption(view, R.id.measure_add_dlg_options_before_position, header.getNumber(), false);
		this.fillOption(view, R.id.measure_add_dlg_options_after_position, (header.getNumber() + 1), false);
		this.fillOption(view, R.id.measure_add_dlg_options_at_end, (song.countMeasureHeaders() + 1), true);
	}

	public void fillOption(View view, int id, Integer value, boolean selected) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(selected);
	}
	
	public int findSelectedMeasureNumber(View view) {
		RadioGroup optionsGroup = (RadioGroup) view.findViewById(R.id.measure_add_dlg_options_group);
		
		int radioButtonId = optionsGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) optionsGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return 1;
	}
	
	public void processAction(TGSong song, Integer number, Integer count) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGAddMeasureListAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, count);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, number);
		tgActionProcessor.process();
	}
}
