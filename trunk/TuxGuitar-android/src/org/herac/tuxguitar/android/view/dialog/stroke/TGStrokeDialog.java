package org.herac.tuxguitar.android.view.dialog.stroke;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGChangeStrokeAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGStroke;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class TGStrokeDialog extends TGDialog {

	public TGStrokeDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_stroke_dialog, null);
		
		this.fillDirection(view, beat);
		this.fillDurations(view, beat);
		this.initializeDurationsState(view, beat);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.stroke_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, findSelectedDirection(view), findSelectedDuration(view));
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
	
	public TGSelectableItem[] createDirectionValues() {
		TGSelectableItem[] selectableItems = new TGSelectableItem[] {
			new TGSelectableItem(Integer.valueOf( TGStroke.STROKE_NONE ), getString(R.string.stroke_dlg_direction_none)),
			new TGSelectableItem(Integer.valueOf( TGStroke.STROKE_UP ), getString(R.string.stroke_dlg_direction_up)),
			new TGSelectableItem(Integer.valueOf( TGStroke.STROKE_DOWN ), getString(R.string.stroke_dlg_direction_down))
		};
		return selectableItems;
	}
	
	public void fillDirection(final View dlgView, TGBeat beat) {
		int selection = (beat != null ? beat.getStroke().getDirection() : TGStroke.STROKE_NONE);
		
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createDirectionValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) dlgView.findViewById(R.id.stroke_dlg_direction_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)), false);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				updateDurationsState(dlgView, findSelectedDirection(dlgView) != TGStroke.STROKE_NONE);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				updateDurationsState(dlgView, findSelectedDirection(dlgView) != TGStroke.STROKE_NONE);
			}
		});
	}
	
	public int findSelectedDirection(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.stroke_dlg_direction_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillDurations(View view, TGBeat beat) {
		int selection = (beat != null && beat.getStroke().getDirection() != TGStroke.STROKE_NONE ? beat.getStroke().getValue() : TGDuration.SIXTEENTH);
		
		this.fillDuration(view, R.id.stroke_dlg_duration_4, TGDuration.QUARTER, selection);
		this.fillDuration(view, R.id.stroke_dlg_duration_8, TGDuration.EIGHTH, selection);
		this.fillDuration(view, R.id.stroke_dlg_duration_16, TGDuration.SIXTEENTH, selection);
		this.fillDuration(view, R.id.stroke_dlg_duration_32, TGDuration.THIRTY_SECOND, selection);
		this.fillDuration(view, R.id.stroke_dlg_duration_64, TGDuration.SIXTY_FOURTH, selection);
	}

	public void fillDuration(View view, int id, int value, int selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration(View view) {
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.stroke_dlg_duration_group);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return 0;
	}
	
	public void initializeDurationsState(View view, TGBeat beat) {
		this.updateDurationsState(view, (beat != null && beat.getStroke().getDirection() != TGStroke.STROKE_NONE));
	}
	
	public void updateDurationsState(View view, boolean enabled) {
		this.updateDurationsState(view, R.id.stroke_dlg_duration_4, enabled);
		this.updateDurationsState(view, R.id.stroke_dlg_duration_8, enabled);
		this.updateDurationsState(view, R.id.stroke_dlg_duration_16, enabled);
		this.updateDurationsState(view, R.id.stroke_dlg_duration_32, enabled);
		this.updateDurationsState(view, R.id.stroke_dlg_duration_64, enabled);
	}
	
	public void updateDurationsState(View view, int id, boolean enabled) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setEnabled(enabled);
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, int direction, int value) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeStrokeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, direction);
		tgActionProcessor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_VALUE, value);
		tgActionProcessor.process();
	}
}
