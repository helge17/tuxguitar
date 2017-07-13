package org.herac.tuxguitar.android.view.dialog.tripletFeel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class TGTripletFeelDialog extends TGModalFragment {
	
	public TGTripletFeelDialog() {
		super(R.layout.view_triplet_feel_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.triplet_feel_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTripletFeelDialog.this.changeTripletFeel();
				TGTripletFeelDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		TGMeasureHeader header = this.getHeader();

		this.updateRadio((RadioButton) this.getView().findViewById(R.id.triplet_feel_dlg_none), TGMeasureHeader.TRIPLET_FEEL_NONE, header.getTripletFeel());
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.triplet_feel_dlg_eighth), TGMeasureHeader.TRIPLET_FEEL_EIGHTH, header.getTripletFeel());
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.triplet_feel_dlg_sixteenth), TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH, header.getTripletFeel());
		
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.triplet_feel_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
	}
	
	public void updateRadio(RadioButton button, Integer value, Integer selection) {
		button.setTag(Integer.valueOf(value));
		button.setChecked(selection != null && selection.equals(value));
	}

	public Integer parseTripletFeelValue() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.triplet_feel_dlg_value);
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGMeasureHeader.TRIPLET_FEEL_NONE;
	}
	
	public Boolean parseApplyToEnd() {
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.triplet_feel_dlg_options_apply_to_end);
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeTripletFeel() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTripletFeelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.getHeader());
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, this.parseTripletFeelValue());
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, this.parseApplyToEnd());
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}
}
