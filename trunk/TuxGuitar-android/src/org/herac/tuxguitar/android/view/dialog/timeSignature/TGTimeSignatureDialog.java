package org.herac.tuxguitar.android.view.dialog.timeSignature;

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
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;

import java.util.ArrayList;
import java.util.List;

public class TGTimeSignatureDialog extends TGModalFragment {

	public TGTimeSignatureDialog() {
		super(R.layout.view_time_signature_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.time_signature_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTimeSignatureDialog.this.changeTimeSignature();
				TGTimeSignatureDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		ArrayAdapter<TGSelectableItem> numeratorAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createNumeratorValues());
		numeratorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner numerator = (Spinner) this.getView().findViewById(R.id.time_signature_dlg_ts_numerator_value);
		numerator.setAdapter(numeratorAdapter);
		numerator.setSelection(numeratorAdapter.getPosition(new TGSelectableItem(header.getTimeSignature().getNumerator(), null)));
		
		ArrayAdapter<TGSelectableItem> denominatorAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createDenominatorValues());
		denominatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner denominator = (Spinner) this.getView().findViewById(R.id.time_signature_dlg_ts_denominator_value);
		denominator.setAdapter(denominatorAdapter);
		denominator.setSelection(denominatorAdapter.getPosition(new TGSelectableItem(header.getTimeSignature().getDenominator().getValue(), null)));
		
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.time_signature_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
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
	
	public TGTimeSignature parseTimeSignature() {
		Spinner numerator = (Spinner) this.getView().findViewById(R.id.time_signature_dlg_ts_numerator_value);
		Spinner denominator = (Spinner) this.getView().findViewById(R.id.time_signature_dlg_ts_denominator_value);

		TGTimeSignature tgTimeSignature = getSongManager().getFactory().newTimeSignature();
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
	
	public Boolean parseApplyToEnd() {
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.time_signature_dlg_options_apply_to_end);
		return Boolean.valueOf(applyToEnd.isChecked());
	}
	
	public void changeTimeSignature() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.getHeader());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, this.parseTimeSignature());
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, this.parseApplyToEnd());
		tgActionProcessor.processOnNewThread();
	}

	public TGSongManager getSongManager() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}
}
