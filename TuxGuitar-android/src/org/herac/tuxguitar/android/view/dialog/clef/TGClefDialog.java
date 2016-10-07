package org.herac.tuxguitar.android.view.dialog.clef;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class TGClefDialog extends TGDialog {
	
	public TGClefDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_clef_dialog, null);
		
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createClefValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner spinner = (Spinner) view.findViewById(R.id.clef_dlg_clef_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(measure.getClef(), null)));
		
		final CheckBox applyToEnd = (CheckBox) view.findViewById(R.id.clef_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.clef_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeClef(parseClefValue(spinner), parseApplyToEnd(applyToEnd), song, track, measure);
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
	
	public TGSelectableItem[] createClefValues() {
		return new TGSelectableItem[]  {
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_TREBLE), getString(R.string.clef_dlg_clef_value_treble)),
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_BASS), getString(R.string.clef_dlg_clef_value_bass)),
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_TENOR), getString(R.string.clef_dlg_clef_value_tenor)),
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_ALTO), getString(R.string.clef_dlg_clef_value_alto))
		};
	}
	
	public Integer parseClefValue(Spinner clef) {
		return (Integer) ((TGSelectableItem)clef.getSelectedItem()).getItem();
	}
	
	public Boolean parseApplyToEnd(CheckBox applyToEnd) {
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeClef(Integer value, Boolean applyToEnd, TGSong song, TGTrack track, TGMeasure measure) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeClefAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, value);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
