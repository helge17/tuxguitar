package org.herac.tuxguitar.android.view.dialog.clef;

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
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGClefDialog extends TGModalFragment {
	
	public TGClefDialog() {
		super(R.layout.view_clef_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.clef_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGClefDialog.this.changeClef();
				TGClefDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createClefValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.clef_dlg_clef_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(measure.getClef(), null)));
		
		CheckBox applyToEnd = (CheckBox) this.getView().findViewById(R.id.clef_dlg_options_apply_to_end);
		applyToEnd.setChecked(true);
	}
	
	public TGSelectableItem[] createClefValues() {
		return new TGSelectableItem[]  {
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_TREBLE), getString(R.string.clef_dlg_clef_value_treble)),
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_BASS), getString(R.string.clef_dlg_clef_value_bass)),
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_TENOR), getString(R.string.clef_dlg_clef_value_tenor)),
			new TGSelectableItem(Integer.valueOf(TGMeasure.CLEF_ALTO), getString(R.string.clef_dlg_clef_value_alto))
		};
	}
	
	public Integer parseClefValue() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.clef_dlg_clef_value);

		return (Integer) ((TGSelectableItem) spinner.getSelectedItem()).getItem();
	}
	
	public Boolean parseApplyToEnd() {
		CheckBox checkbox = (CheckBox) this.getView().findViewById(R.id.clef_dlg_options_apply_to_end);

		return Boolean.valueOf(checkbox.isChecked());
	}
	
	public void changeClef() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeClefAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.getTrack());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, this.parseClefValue());
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, this.parseApplyToEnd());
		tgActionProcessor.process();
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
