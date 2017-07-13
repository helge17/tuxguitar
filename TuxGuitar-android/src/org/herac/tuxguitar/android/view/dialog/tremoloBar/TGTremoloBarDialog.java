package org.herac.tuxguitar.android.view.dialog.tremoloBar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
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

import java.util.ArrayList;
import java.util.List;

public class TGTremoloBarDialog extends TGModalFragment {

	public TGTremoloBarDialog() {
		super(R.layout.view_tremolo_bar_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.tremolo_bar_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTremoloBarDialog.this.updateEffect();
				TGTremoloBarDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.menu_modal_fragment_button_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTremoloBarDialog.this.cleanEffect();
				TGTremoloBarDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		final List<TGTremoloBarPreset> presets = this.createPresets();
		final TGTremoloBarPreset defaultPreset = this.findDefaultPreset(presets);
		final TGEffectTremoloBar defaultTremoloBar = this.findDefaultTremoloBar(defaultPreset);

		this.fillSelectablePresets(presets, defaultPreset);
		
		if( defaultTremoloBar != null ) {
			this.postWhenReady(new Runnable() {
				public void run() {
					loadTremoloBar(defaultTremoloBar);
				}
			});
		}
	}
	
	public TGEffectTremoloBar findDefaultTremoloBar(TGTremoloBarPreset defaultPreset) {
		TGNote note = this.getNote();
		if( note != null && note.getEffect().isTremoloBar()) {
			return note.getEffect().getTremoloBar();
		}
		return (defaultPreset != null ? defaultPreset.getTremoloBar() : null);
	}
	
	public TGTremoloBarPreset findDefaultPreset(List<TGTremoloBarPreset> presets) {
		TGNote note = this.getNote();
		if( note != null && note.getEffect().isTremoloBar()) {
			return null;
		}
		return presets.get(0);
	}
	
	public List<TGTremoloBarPreset> createPresets() {
		TGSongManager songManager = this.getSongManager();
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
	
	public void fillSelectablePresets(List<TGTremoloBarPreset> presets, TGTremoloBarPreset selection) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectablePresets(presets));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.tremolo_bar_dlg_preset_value);
		spinner.setAdapter(arrayAdapter);
		
		this.updateSelectedPreset(selection);
		this.appendListeners();
	}
	
	@SuppressWarnings("unchecked")
	public void updateSelectedPreset(TGTremoloBarPreset selection) {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.tremolo_bar_dlg_preset_value);
		ArrayAdapter<TGSelectableItem> adapter = (ArrayAdapter<TGSelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new TGSelectableItem(selection, null)), false);
	}
	
	public TGTremoloBarPreset findSelectedPreset() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.tremolo_bar_dlg_preset_value);
		return ((TGTremoloBarPreset) ((TGSelectableItem)spinner.getSelectedItem()).getItem());
	}
	
	public void loadSelectedPreset() {
		TGTremoloBarPreset selection = this.findSelectedPreset();
		if( selection != null ) {
			this.loadTremoloBar(selection.getTremoloBar());
		}
	}
	
	public void loadTremoloBar(TGEffectTremoloBar tremoloBar) {
		TGTremoloBarEditor tgTremoloBarEditor = (TGTremoloBarEditor) this.getView().findViewById(R.id.tremolo_bar_dlg_tremolo_bar_editor);
		tgTremoloBarEditor.loadTremoloBar(tremoloBar);
	}

	public TGEffectTremoloBar createTremoloBar(){
		TGTremoloBarEditor tremoloBarEditor = (TGTremoloBarEditor) this.getView().findViewById(R.id.tremolo_bar_dlg_tremolo_bar_editor);
		TGEffectTremoloBar tremoloBar = tremoloBarEditor.createTremoloBar(getSongManager().getFactory());
		return tremoloBar;
	}
	
	public void appendListeners() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.tremolo_bar_dlg_preset_value);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	loadSelectedPreset();
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	loadSelectedPreset();
		    }
		});
		
		TGTremoloBarEditor tgTremoloBarEditor = (TGTremoloBarEditor) this.getView().findViewById(R.id.tremolo_bar_dlg_tremolo_bar_editor);
		tgTremoloBarEditor.setListener(new TGTremoloBarEditorListener() {
			public void onChange() {
				updateSelectedPreset(null);
			}
		});
	}

	public void cleanEffect() {
		this.updateEffect(null);
	}

	public void updateEffect() {
		this.updateEffect(this.createTremoloBar());
	}

	public void updateEffect(TGEffectTremoloBar effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTremoloBarAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, this.getString());
		tgActionProcessor.setAttribute(TGChangeTremoloBarAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}

	public TGSongManager getSongManager() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}

	public TGMeasure getMeasure() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}

	public TGBeat getBeat() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
	}

	public TGNote getNote() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
	}

	public TGString getString() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
	}
}
