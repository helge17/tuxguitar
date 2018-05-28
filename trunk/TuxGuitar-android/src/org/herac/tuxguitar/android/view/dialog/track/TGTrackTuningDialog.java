package org.herac.tuxguitar.android.view.dialog.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.List;

public class TGTrackTuningDialog extends TGModalFragment {

	private List<TGTrackTuningModel> tuning;
	private List<TGTrackTuningModel[]> tuningPresets;
	private TGTrackTuningActionHandler actionHandler;

	public TGTrackTuningDialog() {
		super(R.layout.view_track_tuning_dialog);

		this.tuning = new ArrayList<TGTrackTuningModel>();
		this.tuningPresets = new ArrayList<TGTrackTuningModel[]>();
		this.actionHandler = new TGTrackTuningActionHandler(this);
	}

	public List<TGTrackTuningModel> getTuning() {
		return this.tuning;
	}

	public TGTrackTuningActionHandler getActionHandler() {
		return this.actionHandler;
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.track_tuning_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_track_tuning, menu);
		menu.findItem(R.id.action_add).setOnMenuItemClickListener(TGTrackTuningDialog.this.getActionHandler().createAddTuningModelAction());
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if( TGTrackTuningDialog.this.updateTrackProperties() ) {
					TGTrackTuningDialog.this.close();
				}
				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final boolean percussionChannel = songManager.isPercussionChannel(song, track.getChannelId());

		this.createTuningPresets();
		this.fillTuningListView();
		this.fillOffset(track);
		this.fillPreset();
		this.fillOptions(songManager, song, track);

		this.updateTuningFromTrack(track);
		this.updateItems(percussionChannel);
	}

	public TGSelectableItem[] createSelectableIntegers(int minimum, int maximum) {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		for(int value = minimum ; value <= maximum ; value ++) {
			String label = this.findActivity().getString(R.string.track_tuning_dlg_offset_select_value, value);
			selectableItems.add(new TGSelectableItem(Integer.valueOf(value), label));
		}
		
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		
		return builtItems;
	}

	public TGSelectableItem[] createSelectableOffsets() {
		return this.createSelectableIntegers(TGTrack.MIN_OFFSET, TGTrack.MAX_OFFSET);
	}

	public TGSelectableItem[] createSelectablePresets() {

		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		selectableItems.add(new TGSelectableItem(null, this.findActivity().getString(R.string.track_tuning_dlg_preset_select_value)));
		for(TGTrackTuningModel[] preset : this.tuningPresets) {
			selectableItems.add(new TGSelectableItem(preset, this.createTuningPresetLabel(preset)));
		}

		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);

		return builtItems;
	}

	public int findSelectedOffset() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_offset_value);

		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}

	public TGTrackTuningModel[] findSelectedPreset() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_preset_value);

		return (TGTrackTuningModel[]) ((TGSelectableItem)spinner.getSelectedItem()).getItem();
	}

	public Boolean findOptionValue(int optionId) {
		return Boolean.valueOf(((CheckBox) this.getView().findViewById(optionId)).isChecked());
	}

	public List<TGString> findSelectedTuning() {
		TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		List<TGString> strings = new ArrayList<TGString>();
		for(int i = 0; i < this.tuning.size(); i ++) {
			strings.add(TGSongManager.newString(songManager.getFactory(),(i + 1), this.tuning.get(i).getValue()));
		}
		return strings;
	}

	public void fillTuningListView() {
		ListView listView = (ListView) this.getView().findViewById(R.id.track_tuning_dlg_list_view);
		listView.setAdapter(new TGTrackTuningAdapter(this, this.getView().getContext()));

		this.updateTuningListView();
	}

	public void fillOffset(TGTrack track) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableOffsets());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_offset_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(track.getOffset()), null)), false);
	}

	public void fillPreset() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectablePresets());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_preset_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				TGTrackTuningDialog.this.onSelectPreset();
			}

			public void onNothingSelected(AdapterView<?> adapterView) {}
		});
	}

	public void fillOptions(final TGSongManager songManager, final TGSong song, final TGTrack track) {
		CheckBox stringTransposition = (CheckBox) this.getView().findViewById(R.id.track_tuning_dlg_options_transpose);
		stringTransposition.setChecked(true);
		stringTransposition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onTransposeOptionChanged(songManager, song, track);
			}
		});
		((CheckBox) this.getView().findViewById(R.id.track_tuning_dlg_options_transpose_apply_to_chords)).setChecked(true);
		((CheckBox) this.getView().findViewById(R.id.track_tuning_dlg_options_transpose_try_keep_strings)).setChecked(true);
	}
	
	public void updateItems(boolean percussionChannel) {
		this.updateOffset(!percussionChannel);
		this.updateOptions(!percussionChannel);
	}
	
	public void updateOffset(boolean enabled) {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_offset_value);
		spinner.setEnabled(enabled);
	}
	
	public void updateOptions(boolean enabled) {
		CheckBox stringTransposition = (CheckBox) this.getView().findViewById(R.id.track_tuning_dlg_options_transpose);
		CheckBox stringTranspositionApplyToChords = (CheckBox) this.getView().findViewById(R.id.track_tuning_dlg_options_transpose_apply_to_chords);
		CheckBox stringTranspositionTryKeepString = (CheckBox) this.getView().findViewById(R.id.track_tuning_dlg_options_transpose_try_keep_strings);
		
		boolean stringTranspositionChecked = stringTransposition.isChecked();
		
		stringTransposition.setEnabled(enabled);
		stringTranspositionApplyToChords.setEnabled(enabled && stringTranspositionChecked);
		stringTranspositionTryKeepString.setEnabled(enabled && stringTranspositionChecked);
		if(!stringTranspositionChecked ) {
			stringTranspositionApplyToChords.setChecked(false);
			stringTranspositionTryKeepString.setChecked(false);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateTuningPresetSelection() {
		TGTrackTuningModel[] selection = null;
		for(TGTrackTuningModel[] preset : this.tuningPresets) {
			if( this.isUsingPreset(preset)) {
				selection = preset;
			}
		}

		TGTrackTuningModel[] currentSelection = this.findSelectedPreset();
		if( selection != currentSelection ) {
			Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_tuning_dlg_preset_value);
			spinner.setSelection(((ArrayAdapter<TGSelectableItem>) spinner.getAdapter()).getPosition(new TGSelectableItem(selection, null)), false);
		}
	}

	private boolean isUsingPreset(TGTrackTuningModel[] preset) {
		if( this.tuning.size() == preset.length ) {
			for(int i = 0 ; i < this.tuning.size(); i ++) {
				if(!this.tuning.get(i).getValue().equals(preset[i].getValue())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public void updateTuningControls() {
		this.updateTuningListView();
		this.updateTuningPresetSelection();
	}

	public void updateTuningListView() {
		ListView listView = (ListView) this.getView().findViewById(R.id.track_tuning_dlg_list_view);
		TGTrackTuningAdapter adapter = (TGTrackTuningAdapter) listView.getAdapter();

		adapter.notifyDataSetChanged();
	}

	public void updateTuningFromTrack(TGTrack track) {
		this.tuning.clear();
		for(int i = 0; i < track.stringCount(); i ++) {
			TGString string = track.getString(i + 1);
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(string.getValue());
			this.tuning.add(model);
		}
		this.updateTuningControls();
	}

	private void updateTuningFromPreset(TGTrackTuningModel[] preset) {
		List<TGTrackTuningModel> models = new ArrayList<TGTrackTuningModel>();
		for(TGTrackTuningModel presetModel : preset) {
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(presetModel.getValue());
			models.add(model);
		}
		this.updateTuningModels(models);
	}

	public void modifyTuningModel(TGTrackTuningModel model, Integer value) {
		if( this.tuning.contains(model)) {
			model.setValue(value);
			this.updateTuningControls();
		}
	}

	public void addTuningModel(TGTrackTuningModel model) {
		if( this.tuning.add(model)) {
			this.updateTuningControls();
		}
	}

	public void removeTuningModel(TGTrackTuningModel model) {
		if( this.tuning.remove(model)) {
			this.updateTuningControls();
		}
	}

	public void updateTuningModels(List<TGTrackTuningModel> models) {
		this.tuning.clear();
		if( this.tuning.addAll(models)) {
			this.updateTuningControls();
		}
	}

	private void onSelectPreset() {
		TGTrackTuningModel[] models = this.findSelectedPreset();
		if( models != null ) {
			this.updateTuningFromPreset(models);
		}
	}

	public void onTransposeOptionChanged(TGSongManager songManager, TGSong song, TGTrack track) {
		boolean percussionChannel = songManager.isPercussionChannel(song, track.getChannelId());
		
		this.updateOptions(!percussionChannel);
	}

	public TGTrackTuningModel[] createTuningPreset(int[] values) {
		TGTrackTuningModel[] models = new TGTrackTuningModel[values.length];
		for(int i = 0 ; i < models.length; i ++) {
			models[i] = new TGTrackTuningModel();
			models[i].setValue(values[i]);
		}
		return models;
	}

	public void createTuningPresets() {
		this.tuningPresets.clear();
		for(int[] tuningValues : TGSongManager.DEFAULT_TUNING_VALUES) {
			this.tuningPresets.add(this.createTuningPreset(tuningValues));
		}
	}

	public String createTuningPresetLabel(TGTrackTuningModel[] tuningPreset) {
		StringBuilder label = new StringBuilder();
		for(int i = 0 ; i < tuningPreset.length; i ++) {
			if( i > 0 ) {
				label.append(" ");
			}
			label.append(TGTrackTuningLabel.valueOf(tuningPreset[tuningPreset.length - i - 1].getValue()));
		}
		return label.toString();
	}

	private boolean hasTuningChanges(List<TGString> newStrings) {
		TGTrack track = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if( track != null ) {
			List<TGString> oldStrings = track.getStrings();
			//check the number of strings
			if (oldStrings.size() != newStrings.size()) {
				return true;
			}
			//check the tuning of strings
			for (int i = 0; i < oldStrings.size(); i++) {
				TGString oldString = (TGString) oldStrings.get(i);
				boolean stringExists = false;
				for (int j = 0; j < newStrings.size(); j++) {
					TGString newString = (TGString) newStrings.get(j);
					if (newString.isEqual(oldString)) {
						stringExists = true;
					}
				}
				if (!stringExists) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasOffsetChanges(Integer offset) {
		TGTrack track = this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if( track != null ) {
			return (offset != null && !offset.equals(track.getOffset()));
		}
		return false;
	}

	public boolean updateTrackProperties() {
		TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), TGChangeTrackTuningAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);

		List<TGString> tuning = this.findSelectedTuning();
		if( this.validateTrackTuning(tuning)) {
			if (this.hasTuningChanges(tuning)) {
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, tuning);
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_STRINGS, findOptionValue(R.id.track_tuning_dlg_options_transpose));
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS, findOptionValue(R.id.track_tuning_dlg_options_transpose_apply_to_chords));
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS, findOptionValue(R.id.track_tuning_dlg_options_transpose_try_keep_strings));
			}

			Integer offset = this.findSelectedOffset();
			if (this.hasOffsetChanges(offset)) {
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, offset);
			}
			tgActionProcessor.process();

			return true;
		}
		return false;
	}

	private boolean validateTrackTuning(List<TGString> strings) {
		if( strings.size() < TGTrack.MIN_STRINGS || strings.size() > TGTrack.MAX_STRINGS ) {
			showErrorMessage(this.findActivity().getString(R.string.track_tuning_dlg_range_error, TGTrack.MIN_STRINGS, TGTrack.MAX_STRINGS));
			return false;
		}
		return true;
	}

	public void showErrorMessage(String message) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, this.findActivity().getString(R.string.track_tuning_dlg_error_title));
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}

	public void postModifyTuningModel(final TGTrackTuningModel model, final Integer value) {
		this.postWhenReady(new Runnable() {
			public void run() {
				TGTrackTuningDialog.this.modifyTuningModel(model, value);
			}
		});
	}

	public void postAddTuningModel(final TGTrackTuningModel model) {
		this.postWhenReady(new Runnable() {
			public void run() {
				TGTrackTuningDialog.this.addTuningModel(model);
			}
		});
	}

	public void postRemoveTuningModel(final TGTrackTuningModel model) {
		this.postWhenReady(new Runnable() {
			public void run() {
				TGTrackTuningDialog.this.removeTuningModel(model);
			}
		});
	}
}
