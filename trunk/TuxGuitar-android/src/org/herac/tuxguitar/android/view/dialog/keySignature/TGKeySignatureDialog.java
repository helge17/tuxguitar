package org.herac.tuxguitar.android.view.dialog.keySignature;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGKeySignatureDialog extends TGModalFragment {

	public TGKeySignatureDialog() {
		super(R.layout.view_key_signature_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.key_signature_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGKeySignatureDialog.this.changeKeySignature();
				TGKeySignatureDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createKeyValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.key_signature_dlg_ks_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(this.getMeasure().getKeySignature(), null)));
		
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.key_signature_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
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
	
	public Integer parseKeySignatureValue() {
		Spinner keySignature = (Spinner) this.getView().findViewById(R.id.key_signature_dlg_ks_value);
		return (Integer) ((TGSelectableItem) keySignature.getSelectedItem()).getItem();
	}
	
	public Boolean parseApplyToEnd() {
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.key_signature_dlg_options_apply_to_end);
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeKeySignature() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeKeySignatureAction.NAME);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, this.parseKeySignatureValue());
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, this.parseApplyToEnd());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.getTrack());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGTrack getTrack() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}

	public TGMeasure getMeasure() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}
}
