package app.tuxguitar.android.view.dialog.tempo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import app.tuxguitar.android.R;
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTempoBase;

public class TGTempoDialog extends TGModalFragment {

	// possible tempo bases:
	private final TGTempoBase tempoBase[] = TGTempoBase.getTempoBases();

	public TGTempoDialog() {
		super(R.layout.view_tempo_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.tempo_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTempoDialog.this.changeTempo();
				TGTempoDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		TGTempo tempo = this.getHeader().getTempo();

		final RadioButton[] tempoBaseButton = new RadioButton[tempoBase.length];
		RadioGroup tempoBaseGroup = (RadioGroup) this.getView().findViewById(R.id.tempo_dlg_tempo_base);
		for (int i=0; i<tempoBase.length; i++) {
			tempoBaseButton[i] = new RadioButton(getContext());
			tempoBaseGroup.addView(tempoBaseButton[i]);
			tempoBaseButton[i].setId(i);
			String iconName = "duration_" + tempoBase[i].getBase();
			if(tempoBase[i].isDotted()) iconName += "dotted";
			int iconId = getContext().getResources().getIdentifier(iconName, "drawable", getContext().getPackageName());
			tempoBaseButton[i].setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(iconId, null), null, null, null);
			String buttonText = " 1/" + tempoBase[i].getBase();
			if(tempoBase[i].isDotted()) buttonText += "•";
			tempoBaseButton[i].setText(buttonText);
			if ( (tempo.getBase() == tempoBase[i].getBase()) && (tempo.isDotted() == tempoBase[i].isDotted()) ) {
				tempoBaseButton[i].setChecked(true);
			} else {
				tempoBaseButton[i].setChecked(false);
			}
		}

		ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createTempoValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) this.getView().findViewById(R.id.tempo_dlg_tempo_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(Integer.valueOf(tempo.getRawValue())));

		int applyToDefault = TGChangeTempoRangeAction.APPLY_TO_ALL;
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.tempo_dlg_options_apply_to_song), TGChangeTempoRangeAction.APPLY_TO_ALL, applyToDefault);
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.tempo_dlg_options_apply_to_end), TGChangeTempoRangeAction.APPLY_TO_END, applyToDefault);
		this.updateRadio((RadioButton) this.getView().findViewById(R.id.tempo_dlg_options_apply_to_next_marker), TGChangeTempoRangeAction.APPLY_TO_NEXT, applyToDefault);
	}

	public Integer[] createTempoValues() {
		int length = ((TGChangeTempoRangeAction.MAX_TEMPO - TGChangeTempoRangeAction.MIN_TEMPO) + 1);
		Integer[] items = new Integer[length];
		for (int i = 0; i < length; i++) {
			items[i] = Integer.valueOf(i + TGChangeTempoRangeAction.MIN_TEMPO);
		}
		return items;
	}

	public void updateRadio(RadioButton button, Integer value, Integer selection) {
		button.setTag(Integer.valueOf(value));
		button.setChecked(selection != null && selection.equals(value));
	}

	public int parseTempoValue() {
		final Spinner spinner = (Spinner) this.getView().findViewById(R.id.tempo_dlg_tempo_value);

		return ((Integer) spinner.getSelectedItem()).intValue();
	}

	public int parseTempoBase() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.tempo_dlg_tempo_base);
		return tempoBase[radioGroup.getCheckedRadioButtonId()].getBase();
	}

	public boolean parseTempoBaseDotted() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.tempo_dlg_tempo_base);
		return tempoBase[radioGroup.getCheckedRadioButtonId()].isDotted();
	}

	public Integer parseApplyTo() {
		RadioGroup radioGroup = (RadioGroup) this.getView().findViewById(R.id.tempo_dlg_options_group);

		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGChangeTempoRangeAction.APPLY_TO_ALL;
	}

	public void changeTempo() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTempoRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.getHeader());
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, this.parseTempoValue());
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE, this.parseTempoBase());
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE_DOTTED, this.parseTempoBaseDotted());
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, this.parseApplyTo());
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}

	private class TempoBase {
		private int base;
		private boolean dotted;

		TempoBase(int base, boolean dotted) {
			this.base = base;
			this.dotted = dotted;
		}
	}
}
