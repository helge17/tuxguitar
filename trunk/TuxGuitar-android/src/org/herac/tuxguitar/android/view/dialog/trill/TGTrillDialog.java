package org.herac.tuxguitar.android.view.dialog.trill;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class TGTrillDialog extends TGDialog {

	public TGTrillDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGNote note = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		final TGString string = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_trill_dialog, null);
		
		this.fillFret(view, note);
		this.fillDurations(view, note);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.trill_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, createTrill(view, songManager));
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
	
	public TGSelectableItem[] createFretValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for (int i = 0; i <= 100; i++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillFret(View view, TGNote note) {
		int selection = 0;
		if( note != null ) {
			selection = (note.getEffect().isTrill() ? note.getEffect().getTrill().getFret() : note.getValue());
		}
		
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createFretValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) view.findViewById(R.id.trill_dlg_fret_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)));
	}
	
	public int findSelectedFret(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.trill_dlg_fret_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public void fillDurations(View view, TGNote note) {
		int duration = TGDuration.EIGHTH;
		if( note != null && note.getEffect().isTrill() ){
			duration = note.getEffect().getTrill().getDuration().getValue();
		}
		
		this.fillDuration(view, R.id.trill_dlg_duration_16, TGDuration.SIXTEENTH, duration);
		this.fillDuration(view, R.id.trill_dlg_duration_32, TGDuration.THIRTY_SECOND, duration);
		this.fillDuration(view, R.id.trill_dlg_duration_64, TGDuration.SIXTY_FOURTH, duration);
	}

	public void fillDuration(View view, int id, int value, int selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration(View view) {
		RadioGroup optionsGroup = (RadioGroup) view.findViewById(R.id.trill_dlg_duration_group);
		
		int radioButtonId = optionsGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) optionsGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGDuration.EIGHTH;
	}
	
	public TGEffectTrill createTrill(View view, TGSongManager songManager){
		TGEffectTrill tgEffectTrill = songManager.getFactory().newEffectTrill();
		tgEffectTrill.setFret(findSelectedFret(view));
		tgEffectTrill.getDuration().setValue(findSelectedDuration(view));
		return tgEffectTrill;
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, TGString string, TGEffectTrill effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTrillNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTrillNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
