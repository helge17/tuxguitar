package org.herac.tuxguitar.android.view.dialog.tripletFeel;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TGTripletFeelDialog extends TGDialog {
	
	public TGTripletFeelDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_triplet_feel_dialog, null);
		
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.triplet_feel_dlg_value);
		
		this.updateRadio((RadioButton)view.findViewById(R.id.triplet_feel_dlg_none), TGMeasureHeader.TRIPLET_FEEL_NONE, header.getTripletFeel());
		this.updateRadio((RadioButton)view.findViewById(R.id.triplet_feel_dlg_eighth), TGMeasureHeader.TRIPLET_FEEL_EIGHTH, header.getTripletFeel());
		this.updateRadio((RadioButton)view.findViewById(R.id.triplet_feel_dlg_sixteenth), TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH, header.getTripletFeel());
		
		final CheckBox applyToEnd = (CheckBox) view.findViewById(R.id.triplet_feel_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.triplet_feel_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeTripletFeel(song, header, parseTripletFeelValue(radioGroup), parseApplyToEnd(applyToEnd));
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
	
	public void updateRadio(RadioButton button, Integer value, Integer selection) {
		button.setTag(Integer.valueOf(value));
		button.setChecked(selection != null && selection.equals(value));
	}

	public Integer parseTripletFeelValue(RadioGroup radioGroup) {
		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGMeasureHeader.TRIPLET_FEEL_NONE;
	}
	
	public Boolean parseApplyToEnd(CheckBox applyToEnd) {
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeTripletFeel(TGSong song, TGMeasureHeader header, Integer tripletFeel, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTripletFeelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, tripletFeel);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
