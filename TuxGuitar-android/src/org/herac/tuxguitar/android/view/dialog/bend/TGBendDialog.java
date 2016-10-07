package org.herac.tuxguitar.android.view.dialog.bend;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TGBendDialog extends TGDialog {

	public TGBendDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGMeasure measure = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGNote note = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		final TGString string = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final List<TGBendPreset> presets = this.createPresets(songManager);
		final TGBendPreset defaultPreset = this.findDefaultPreset(note, presets);
		final TGEffectBend defaultBend = this.findDefaultBend(note, defaultPreset);
		
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_bend_dialog, null);
		
		this.fillSelectablePresets(view, presets, defaultPreset);
		
		if( defaultBend != null ) {
			view.post(new Runnable() {
				public void run() {
					loadBend(view, defaultBend);
				}
			});
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.bend_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, createBend(view, songManager));
				dialog.dismiss();
			}
		});
		builder.setNeutralButton(R.string.global_button_clean, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, null);
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
	
	public TGEffectBend findDefaultBend(TGNote note, TGBendPreset defaultPreset) {
		if( note != null && note.getEffect().isBend()) {
			return note.getEffect().getBend();
		}
		return (defaultPreset != null ? defaultPreset.getBend() : null);
	}
	
	public TGBendPreset findDefaultPreset(TGNote note, List<TGBendPreset> presets) {
		if( note != null && note.getEffect().isBend()) {
			return null;
		}
		return presets.get(0);
	}
	
	public List<TGBendPreset> createPresets(TGSongManager songManager){
		List<TGBendPreset> presets = new ArrayList<TGBendPreset>();
		TGFactory factory = songManager.getFactory();
		TGBendPreset preset = null;
		
		preset = new TGBendPreset(getString(R.string.bend_dlg_preset_bend), factory.newEffectBend());
		preset.getBend().addPoint(0,0);
		preset.getBend().addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		presets.add(preset);
		
		preset = new TGBendPreset(getString(R.string.bend_dlg_preset_bend_release), factory.newEffectBend());
		preset.getBend().addPoint(0,0);
		preset.getBend().addPoint(3,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(9,0);
		preset.getBend().addPoint(12,0);
		presets.add(preset);
		
		preset = new TGBendPreset(getString(R.string.bend_dlg_preset_bend_release_bend), factory.newEffectBend());
		preset.getBend().addPoint(0,0);
		preset.getBend().addPoint(2,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(6,0);
		preset.getBend().addPoint(8,0);
		preset.getBend().addPoint(10,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		presets.add(preset);
		
		preset = new TGBendPreset(getString(R.string.bend_dlg_preset_prebend), factory.newEffectBend());
		preset.getBend().addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		presets.add(preset);
		
		preset = new TGBendPreset(getString(R.string.bend_dlg_preset_prebend_release), factory.newEffectBend());
		preset.getBend().addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
		preset.getBend().addPoint(8,0);
		preset.getBend().addPoint(12,0);
		presets.add(preset);
		
		return presets;
	}
	
	public TGSelectableItem[] createSelectablePresets(List<TGBendPreset> presets) {
		List<TGSelectableItem> selectablePresets = new ArrayList<TGSelectableItem>();
		selectablePresets.add(new TGSelectableItem(null, getString(R.string.global_spinner_select_option)));
		
		for(TGBendPreset preset : presets) {
			selectablePresets.add(new TGSelectableItem(preset, preset.getName()));
		}
		
		TGSelectableItem[] builtItems = new TGSelectableItem[selectablePresets.size()];
		selectablePresets.toArray(builtItems);
		
		return builtItems;
	}
	
	public void fillSelectablePresets(final View dlgView, List<TGBendPreset> presets, TGBendPreset selection) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectablePresets(presets));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner spinner = (Spinner) dlgView.findViewById(R.id.bend_dlg_preset_value);
		spinner.setAdapter(arrayAdapter);
		
		this.updateSelectedPreset(spinner, selection);
		this.appendListeners(dlgView, spinner);
	}
	
	@SuppressWarnings("unchecked")
	public void updateSelectedPreset(Spinner spinner, TGBendPreset selection) {
		ArrayAdapter<TGSelectableItem> adapter = (ArrayAdapter<TGSelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new TGSelectableItem(selection, null)), false);
	}
	
	public TGBendPreset findSelectedPreset(Spinner spinner) {
		return ((TGBendPreset) ((TGSelectableItem)spinner.getSelectedItem()).getItem());
	}
	
	public void loadSelectedPreset(View view, Spinner spinner) {
		TGBendPreset selection = this.findSelectedPreset(spinner);
		if( selection != null ) {
			this.loadBend(view, selection.getBend());
		}
	}
	
	public void loadBend(View view, TGEffectBend bend) {
		TGBendEditor tgBendEditor = (TGBendEditor) view.findViewById(R.id.bend_dlg_bend_editor);
		tgBendEditor.loadBend(bend);
	}

	public TGEffectBend createBend(View view, TGSongManager songManager){
		TGBendEditor bendEditor = (TGBendEditor) view.findViewById(R.id.bend_dlg_bend_editor);
		TGEffectBend bend = bendEditor.createBend(songManager.getFactory());
		return bend;
	}
	
	public void appendListeners(final View dlgView, final Spinner spinner) {
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	loadSelectedPreset(dlgView, spinner);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	loadSelectedPreset(dlgView, spinner);
		    }
		});
		
		TGBendEditor tgBendEditor = (TGBendEditor) dlgView.findViewById(R.id.bend_dlg_bend_editor);
		tgBendEditor.setListener(new TGBendEditorListener() {
			public void onChange() {
				updateSelectedPreset(spinner, null);
			}
		});
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, TGString string, TGEffectBend effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeBendNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeBendNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
