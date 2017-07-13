package org.herac.tuxguitar.android.view.dialog.stroke;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGChangeStrokeAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGStroke;

public class TGStrokeDialog extends TGModalFragment {

	public TGStrokeDialog() {
		super(R.layout.view_stroke_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.stroke_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGStrokeDialog.this.processAction();
				TGStrokeDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillDirection();
		this.fillDurations();
		this.initializeDurationsState();
	}
	
	public TGSelectableItem[] createDirectionValues() {
		TGSelectableItem[] selectableItems = new TGSelectableItem[] {
			new TGSelectableItem(Integer.valueOf( TGStroke.STROKE_NONE ), getString(R.string.stroke_dlg_direction_none)),
			new TGSelectableItem(Integer.valueOf( TGStroke.STROKE_UP ), getString(R.string.stroke_dlg_direction_up)),
			new TGSelectableItem(Integer.valueOf( TGStroke.STROKE_DOWN ), getString(R.string.stroke_dlg_direction_down))
		};
		return selectableItems;
	}
	
	public void fillDirection() {
		TGBeat beat = this.getBeat();
		int selection = (beat != null ? beat.getStroke().getDirection() : TGStroke.STROKE_NONE);
		
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createDirectionValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.stroke_dlg_direction_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)), false);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				updateDurationsState(findSelectedDirection() != TGStroke.STROKE_NONE);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				updateDurationsState(findSelectedDirection() != TGStroke.STROKE_NONE);
			}
		});
	}
	
	public int findSelectedDirection() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.stroke_dlg_direction_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillDurations() {
		TGBeat beat = this.getBeat();
		int selection = (beat != null && beat.getStroke().getDirection() != TGStroke.STROKE_NONE ? beat.getStroke().getValue() : TGDuration.SIXTEENTH);
		
		this.fillDuration(R.id.stroke_dlg_duration_4, TGDuration.QUARTER, selection);
		this.fillDuration(R.id.stroke_dlg_duration_8, TGDuration.EIGHTH, selection);
		this.fillDuration(R.id.stroke_dlg_duration_16, TGDuration.SIXTEENTH, selection);
		this.fillDuration(R.id.stroke_dlg_duration_32, TGDuration.THIRTY_SECOND, selection);
		this.fillDuration(R.id.stroke_dlg_duration_64, TGDuration.SIXTY_FOURTH, selection);
	}

	public void fillDuration(int id, int value, int selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.stroke_dlg_duration_group);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return 0;
	}
	
	public void initializeDurationsState() {
		TGBeat beat = this.getBeat();
		this.updateDurationsState((beat != null && beat.getStroke().getDirection() != TGStroke.STROKE_NONE));
	}
	
	public void updateDurationsState(boolean enabled) {
		this.updateDurationsState(R.id.stroke_dlg_duration_4, enabled);
		this.updateDurationsState(R.id.stroke_dlg_duration_8, enabled);
		this.updateDurationsState(R.id.stroke_dlg_duration_16, enabled);
		this.updateDurationsState(R.id.stroke_dlg_duration_32, enabled);
		this.updateDurationsState(R.id.stroke_dlg_duration_64, enabled);
	}
	
	public void updateDurationsState(int id, boolean enabled) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setEnabled(enabled);
	}
	
	public void processAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeStrokeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, this.findSelectedDirection());
		tgActionProcessor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_VALUE, this.findSelectedDuration());
		tgActionProcessor.process();
	}

	public TGMeasure getMeasure() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}

	public TGBeat getBeat() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
	}
}
