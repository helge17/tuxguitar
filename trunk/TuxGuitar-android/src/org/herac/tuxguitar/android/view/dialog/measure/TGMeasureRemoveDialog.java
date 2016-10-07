package org.herac.tuxguitar.android.view.dialog.measure;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TGMeasureRemoveDialog extends TGDialog {

	public TGMeasureRemoveDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_measure_remove_dialog, null);
		
		this.fillRanges(view, song, header);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.measure_remove_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(song, findSelectedMeasure1(view), findSelectedMeasure2(view));
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
	
	public void fillRanges(View view, TGSong song, TGMeasureHeader measure) {
		final int minimum = 1;
		final int maximum = song.countMeasureHeaders();
		
		final Spinner spinner1 = (Spinner) view.findViewById(R.id.measure_remove_dlg_from_value);
		final Spinner spinner2 = (Spinner) view.findViewById(R.id.measure_remove_dlg_to_value);
		
		this.fillSpinner(spinner1, minimum, maximum);
		this.fillSpinner(spinner2, minimum, maximum);
		
		this.updateSpinnerSelection(spinner1, measure.getNumber());
		this.updateSpinnerSelection(spinner2, measure.getNumber());
		
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	validateSpinner1Selection(spinner1, spinner2, minimum, maximum);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	validateSpinner1Selection(spinner1, spinner2, minimum, maximum);
		    }
		});
		
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	validateSpinner2Selection(spinner1, spinner2, minimum, maximum);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	validateSpinner2Selection(spinner1, spinner2, minimum, maximum);
		    }
		});
	}
	
	public void validateSpinner1Selection(Spinner spinner1, Spinner spinner2, int minimum, int maximum) {
		int selection1 = findSelectedValue(spinner1);
		int selection2 = findSelectedValue(spinner2);
		
		if( selection1 < minimum ){
			this.updateSpinnerSelection(spinner1, minimum);
		}else if(selection1 > selection2){
			this.updateSpinnerSelection(spinner1, selection2);
		}
	}
	
	public void validateSpinner2Selection(Spinner spinner1, Spinner spinner2, int minimum, int maximum) {
		int selection1 = findSelectedValue(spinner1);
		int selection2 = findSelectedValue(spinner2);
		
		if( selection2 < selection1){
			this.updateSpinnerSelection(spinner2, selection1);
		}else if(selection2 > maximum){
			this.updateSpinnerSelection(spinner2, selection1);
		}
	}
	
	public int findSelectedMeasure1(View view) {
		return this.findSelectedValue((Spinner) view.findViewById(R.id.measure_remove_dlg_from_value));
	}
	
	public int findSelectedMeasure2(View view) {
		return this.findSelectedValue((Spinner) view.findViewById(R.id.measure_remove_dlg_to_value));
	}
	
	public int findSelectedValue(Spinner spinner) {
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public void updateSpinnerSelection(Spinner spinner, int selection) {
		ArrayAdapter<TGSelectableItem> adapter = (ArrayAdapter<TGSelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)), false);
	}
	
	public void processAction(TGSong song, Integer measure1, Integer measure2) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGRemoveMeasureRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		tgActionProcessor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		tgActionProcessor.process();
	}
}
