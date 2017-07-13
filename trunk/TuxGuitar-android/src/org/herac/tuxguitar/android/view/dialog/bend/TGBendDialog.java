package org.herac.tuxguitar.android.view.dialog.bend;

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
import org.herac.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;

import java.util.ArrayList;
import java.util.List;

public class TGBendDialog extends TGModalFragment {

	public TGBendDialog() {
		super(R.layout.view_bend_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.bend_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGBendDialog.this.updateEffect();
				TGBendDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.menu_modal_fragment_button_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGBendDialog.this.cleanEffect();
				TGBendDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		final List<TGBendPreset> presets = this.createPresets();
		final TGBendPreset defaultPreset = this.findDefaultPreset(presets);
		final TGEffectBend defaultBend = this.findDefaultBend(defaultPreset);

		this.fillSelectablePresets(presets, defaultPreset);
		
		if( defaultBend != null ) {
			this.postWhenReady(new Runnable() {
				public void run() {
					loadBend(defaultBend);
				}
			});
		}
	}
	
	public TGEffectBend findDefaultBend(TGBendPreset defaultPreset) {
		TGNote note = this.getNote();
		if( note != null && note.getEffect().isBend()) {
			return note.getEffect().getBend();
		}
		return (defaultPreset != null ? defaultPreset.getBend() : null);
	}
	
	public TGBendPreset findDefaultPreset(List<TGBendPreset> presets) {
		TGNote note = this.getNote();
		if( note != null && note.getEffect().isBend()) {
			return null;
		}
		return presets.get(0);
	}
	
	public List<TGBendPreset> createPresets() {
		TGSongManager songManager = this.getSongManager();
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
	
	public void fillSelectablePresets(List<TGBendPreset> presets, TGBendPreset selection) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectablePresets(presets));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.bend_dlg_preset_value);
		spinner.setAdapter(arrayAdapter);
		
		this.updateSelectedPreset(selection);
		this.appendListeners();
	}
	
	@SuppressWarnings("unchecked")
	public void updateSelectedPreset(TGBendPreset selection) {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.bend_dlg_preset_value);
		ArrayAdapter<TGSelectableItem> adapter = (ArrayAdapter<TGSelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new TGSelectableItem(selection, null)), false);
	}
	
	public TGBendPreset findSelectedPreset() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.bend_dlg_preset_value);
		return ((TGBendPreset) ((TGSelectableItem)spinner.getSelectedItem()).getItem());
	}
	
	public void loadSelectedPreset() {
		TGBendPreset selection = this.findSelectedPreset();
		if( selection != null ) {
			this.loadBend(selection.getBend());
		}
	}
	
	public void loadBend(TGEffectBend bend) {
		TGBendEditor tgBendEditor = (TGBendEditor) this.getView().findViewById(R.id.bend_dlg_bend_editor);
		tgBendEditor.loadBend(bend);
	}

	public TGEffectBend createBend() {
		TGBendEditor bendEditor = (TGBendEditor) this.getView().findViewById(R.id.bend_dlg_bend_editor);
		TGEffectBend bend = bendEditor.createBend(this.getSongManager().getFactory());
		return bend;
	}
	
	public void appendListeners() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.bend_dlg_preset_value);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	loadSelectedPreset();
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	loadSelectedPreset();
		    }
		});
		
		TGBendEditor tgBendEditor = (TGBendEditor) this.getView().findViewById(R.id.bend_dlg_bend_editor);
		tgBendEditor.setListener(new TGBendEditorListener() {
			public void onChange() {
				updateSelectedPreset(null);
			}
		});
	}

	public void cleanEffect() {
		this.updateEffect(null);
	}

	public void updateEffect() {
		this.updateEffect(this.createBend());
	}

	public void updateEffect(TGEffectBend effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeBendNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, this.getString());
		tgActionProcessor.setAttribute(TGChangeBendNoteAction.ATTRIBUTE_EFFECT, effect);
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
