package org.herac.tuxguitar.android.view.dialog.keySignature;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class TGKeySignatureDialog extends TGDialog {

	public TGKeySignatureDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_key_signature_dialog, null);
		
		final TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createKeyValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner spinner = (Spinner) view.findViewById(R.id.key_signature_dlg_ks_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(measure.getKeySignature(), null)));
		
		final CheckBox applyToEnd = (CheckBox) view.findViewById(R.id.key_signature_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
		
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.key_signature_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeKeySignature(parseSpinnerValue(spinner), parseApplyToEnd(applyToEnd), track, measure);
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
	
	public TGSelectableItem[] createKeyValues() {
		return new TGSelectableItem[]  {
			new TGSelectableItem(Integer.valueOf(0), getString(R.string.key_signature_dlg_ks_value_natural)),
			new TGSelectableItem(Integer.valueOf(1), getString(R.string.key_signature_dlg_ks_value_sharp_1)),
			new TGSelectableItem(Integer.valueOf(2), getString(R.string.key_signature_dlg_ks_value_sharp_2)),
			new TGSelectableItem(Integer.valueOf(3), getString(R.string.key_signature_dlg_ks_value_sharp_3)),
			new TGSelectableItem(Integer.valueOf(4), getString(R.string.key_signature_dlg_ks_value_sharp_4)),
			new TGSelectableItem(Integer.valueOf(5), getString(R.string.key_signature_dlg_ks_value_sharp_5)),
			new TGSelectableItem(Integer.valueOf(6), getString(R.string.key_signature_dlg_ks_value_sharp_6)),
			new TGSelectableItem(Integer.valueOf(7), getString(R.string.key_signature_dlg_ks_value_sharp_7)),
			new TGSelectableItem(Integer.valueOf(8), getString(R.string.key_signature_dlg_ks_value_flat_1)),
			new TGSelectableItem(Integer.valueOf(9), getString(R.string.key_signature_dlg_ks_value_flat_2)),
			new TGSelectableItem(Integer.valueOf(10), getString(R.string.key_signature_dlg_ks_value_flat_3)),
			new TGSelectableItem(Integer.valueOf(11), getString(R.string.key_signature_dlg_ks_value_flat_4)),
			new TGSelectableItem(Integer.valueOf(12), getString(R.string.key_signature_dlg_ks_value_flat_5)),
			new TGSelectableItem(Integer.valueOf(13), getString(R.string.key_signature_dlg_ks_value_flat_6)),
			new TGSelectableItem(Integer.valueOf(14), getString(R.string.key_signature_dlg_ks_value_flat_7)),
		};
	}
	
	public Integer parseSpinnerValue(Spinner keySignature) {
		return (Integer) ((TGSelectableItem)keySignature.getSelectedItem()).getItem();
	}
	
	public Boolean parseApplyToEnd(CheckBox applyToEnd) {
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeKeySignature(Integer value, Boolean applyToEnd, TGTrack track, TGMeasure measure) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeKeySignatureAction.NAME);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, value);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.processOnNewThread();
	}
}
