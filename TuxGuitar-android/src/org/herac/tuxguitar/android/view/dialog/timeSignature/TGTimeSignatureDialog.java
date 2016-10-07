package org.herac.tuxguitar.android.view.dialog.timeSignature;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class TGTimeSignatureDialog extends TGDialog {

	public TGTimeSignatureDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_time_signature_dialog, null);
		
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		ArrayAdapter<TGSelectableItem> numeratorAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createNumeratorValues());
		numeratorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner numerator = (Spinner) view.findViewById(R.id.time_signature_dlg_ts_numerator_value);
		numerator.setAdapter(numeratorAdapter);
		numerator.setSelection(numeratorAdapter.getPosition(new TGSelectableItem(header.getTimeSignature().getNumerator(), null)));
		
		ArrayAdapter<TGSelectableItem> denominatorAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createDenominatorValues());
		denominatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner denominator = (Spinner) view.findViewById(R.id.time_signature_dlg_ts_denominator_value);
		denominator.setAdapter(denominatorAdapter);
		denominator.setSelection(denominatorAdapter.getPosition(new TGSelectableItem(header.getTimeSignature().getDenominator().getValue(), null)));
		
		final CheckBox applyToEnd = (CheckBox) view.findViewById(R.id.time_signature_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.time_signature_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeTimeSignature(parseTimeSignature(songManager, numerator, denominator), parseApplyToEnd(applyToEnd), song, header);
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
	
	public TGSelectableItem[] createNumeratorValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 1; i <= 32; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public TGSelectableItem[] createDenominatorValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 1; i <= 32; i = i * 2) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public TGTimeSignature parseTimeSignature(TGSongManager songManager, Spinner numerator, Spinner denominator) {
		TGTimeSignature tgTimeSignature = songManager.getFactory().newTimeSignature();
		tgTimeSignature.setNumerator(parseNumeratorValue(numerator));
		tgTimeSignature.getDenominator().setValue(parseDenominatorValue(denominator));
		return tgTimeSignature;
	}
	
	public Integer parseNumeratorValue(Spinner numerator) {
		return (Integer) ((TGSelectableItem) numerator.getSelectedItem()).getItem();
	}
	
	public Integer parseDenominatorValue(Spinner denominator) {
		return (Integer) ((TGSelectableItem) denominator.getSelectedItem()).getItem();
	}
	
	public Boolean parseApplyToEnd(CheckBox applyToEnd) {
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeTimeSignature(TGTimeSignature timeSignature, Boolean applyToEnd, TGSong song, TGMeasureHeader header) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, timeSignature);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
