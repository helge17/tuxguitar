package org.herac.tuxguitar.android.view.dialog.tremoloBar;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TGTremoloBarDialog extends TGDialog {

	public TGTremoloBarDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGNote note = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		final TGString string = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final List<TGTremoloBarPreset> presets = this.createPresets(songManager);
		final TGTremoloBarPreset defaultPreset = this.findDefaultPreset(note, presets);
		final TGEffectTremoloBar defaultTremoloBar = this.findDefaultTremoloBar(note, defaultPreset);
		
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_tremolo_bar_dialog, null);
		
		this.fillSelectablePresets(view, presets, defaultPreset);
		
		if( defaultTremoloBar != null ) {
			view.post(new Runnable() {
				public void run() {
					loadTremoloBar(view, defaultTremoloBar);
				}
			});
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.tremolo_bar_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, createTremoloBar(view, songManager));
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
	
	public TGEffectTremoloBar findDefaultTremoloBar(TGNote note, TGTremoloBarPreset defaultPreset) {
		if( note != null && note.getEffect().isTremoloBar()) {
			return note.getEffect().getTremoloBar();
		}
		return (defaultPreset != null ? defaultPreset.getTremoloBar() : null);
	}
	
	public TGTremoloBarPreset findDefaultPreset(TGNote note, List<TGTremoloBarPreset> presets) {
		if( note != null && note.getEffect().isTremoloBar()) {
			return null;
		}
		return presets.get(0);
	}
	
	public List<TGTremoloBarPreset> createPresets(TGSongManager songManager){
		List<TGTremoloBarPreset> presets = new ArrayList<TGTremoloBarPreset>();
		TGFactory factory = songManager.getFactory();
		TGTremoloBarPreset preset = null;
		
		preset = new TGTremoloBarPreset(getString(R.string.tremolo_bar_dlg_preset_dip), factory.newEffectTremoloBar());
		preset.getTremoloBar().addPoint(0,0);
		preset.getTremoloBar().addPoint(6,-2);
		preset.getTremoloBar().addPoint(12,0);
		presets.add(preset);
		
		preset = new TGTremoloBarPreset(getString(R.string.tremolo_bar_dlg_preset_dive), factory.newEffectTremoloBar());
		preset.getTremoloBar().addPoint(0,0);
		preset.getTremoloBar().addPoint(9,-2);
		preset.getTremoloBar().addPoint(12,-2);
		presets.add(preset);
		
		preset = new TGTremoloBarPreset(getString(R.string.tremolo_bar_dlg_preset_release_up), factory.newEffectTremoloBar());
		preset.getTremoloBar().addPoint(0,-2);
		preset.getTremoloBar().addPoint(9,-2);
		preset.getTremoloBar().addPoint(12,0);
		presets.add(preset);
		
		preset = new TGTremoloBarPreset(getString(R.string.tremolo_bar_dlg_preset_inverted_dip), factory.newEffectTremoloBar());
		preset.getTremoloBar().addPoint(0,0);
		preset.getTremoloBar().addPoint(6,2);
		preset.getTremoloBar().addPoint(12,0);
		presets.add(preset);
		
		preset = new TGTremoloBarPreset(getString(R.string.tremolo_bar_dlg_preset_return), factory.newEffectTremoloBar());
		preset.getTremoloBar().addPoint(0,0);
		preset.getTremoloBar().addPoint(9,2);
		preset.getTremoloBar().addPoint(12,2);
		presets.add(preset);
		
		preset = new TGTremoloBarPreset(getString(R.string.tremolo_bar_dlg_preset_release_down), factory.newEffectTremoloBar());
		preset.getTremoloBar().addPoint(0,2);
		preset.getTremoloBar().addPoint(9,2);
		preset.getTremoloBar().addPoint(12,0);
		presets.add(preset);
		
		return presets;
	}
	
	public TGSelectableItem[] createSelectablePresets(List<TGTremoloBarPreset> presets) {
		List<TGSelectableItem> selectablePresets = new ArrayList<TGSelectableItem>();
		selectablePresets.add(new TGSelectableItem(null, getString(R.string.global_spinner_select_option)));
		
		for(TGTremoloBarPreset preset : presets) {
			selectablePresets.add(new TGSelectableItem(preset, preset.getName()));
		}
		
		TGSelectableItem[] builtItems = new TGSelectableItem[selectablePresets.size()];
		selectablePresets.toArray(builtItems);
		
		return builtItems;
	}
	
	public void fillSelectablePresets(final View dlgView, List<TGTremoloBarPreset> presets, TGTremoloBarPreset selection) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectablePresets(presets));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner spinner = (Spinner) dlgView.findViewById(R.id.tremolo_bar_dlg_preset_value);
		spinner.setAdapter(arrayAdapter);
		
		this.updateSelectedPreset(spinner, selection);
		this.appendListeners(dlgView, spinner);
	}
	
	@SuppressWarnings("unchecked")
	public void updateSelectedPreset(Spinner spinner, TGTremoloBarPreset selection) {
		ArrayAdapter<TGSelectableItem> adapter = (ArrayAdapter<TGSelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new TGSelectableItem(selection, null)), false);
	}
	
	public TGTremoloBarPreset findSelectedPreset(Spinner spinner) {
		return ((TGTremoloBarPreset) ((TGSelectableItem)spinner.getSelectedItem()).getItem());
	}
	
	public void loadSelectedPreset(View view, Spinner spinner) {
		TGTremoloBarPreset selection = this.findSelectedPreset(spinner);
		if( selection != null ) {
			this.loadTremoloBar(view, selection.getTremoloBar());
		}
	}
	
	public void loadTremoloBar(View view, TGEffectTremoloBar tremoloBar) {
		TGTremoloBarEditor tgTremoloBarEditor = (TGTremoloBarEditor) view.findViewById(R.id.tremolo_bar_dlg_tremolo_bar_editor);
		tgTremoloBarEditor.loadTremoloBar(tremoloBar);
	}

	public TGEffectTremoloBar createTremoloBar(View view, TGSongManager songManager){
		TGTremoloBarEditor tremoloBarEditor = (TGTremoloBarEditor) view.findViewById(R.id.tremolo_bar_dlg_tremolo_bar_editor);
		TGEffectTremoloBar tremoloBar = tremoloBarEditor.createTremoloBar(songManager.getFactory());
		return tremoloBar;
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
		
		TGTremoloBarEditor tgTremoloBarEditor = (TGTremoloBarEditor) dlgView.findViewById(R.id.tremolo_bar_dlg_tremolo_bar_editor);
		tgTremoloBarEditor.setListener(new TGTremoloBarEditorListener() {
			public void onChange() {
				updateSelectedPreset(spinner, null);
			}
		});
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, TGString string, TGEffectTremoloBar effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTremoloBarAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTremoloBarAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
