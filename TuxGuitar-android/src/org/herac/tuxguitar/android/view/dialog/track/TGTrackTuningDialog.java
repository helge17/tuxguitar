package org.herac.tuxguitar.android.view.dialog.track;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.List;

public class TGTrackTuningDialog extends TGDialog {
	
	private static final int MAX_STRINGS = 7;
	private static final int MIN_STRINGS = 4;
	private static final int MAX_OCTAVES = 10;
	private static final int MAX_NOTES = 12;
	
	private static final String[] KEY_NAMES = new String[]{
		"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"
	};
	
	private static final int[] TUNING_SPINNER_IDS = new int[]{
		R.id.track_tuning_dlg_string_values_1,
		R.id.track_tuning_dlg_string_values_2,
		R.id.track_tuning_dlg_string_values_3,
		R.id.track_tuning_dlg_string_values_4,
		R.id.track_tuning_dlg_string_values_5,
		R.id.track_tuning_dlg_string_values_6,
		R.id.track_tuning_dlg_string_values_7
	};
	
	public TGTrackTuningDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final List<TGString> strings = createStrings(songManager, track);
		final boolean percussionChannel = songManager.isPercussionChannel(song, track.getChannelId());
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_track_tuning_dialog, null);
		
		this.fillStrings(view, songManager, song, track, strings);
		this.fillOffset(view, track);
		this.fillTunings(view);
		this.fillOptions(view, songManager, song, track);
		
		this.updateItems(view, strings, percussionChannel);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.track_tuning_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				updateTrackProperties(view, songManager, track);
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

	public List<TGString> createStrings(TGSongManager songManager, TGTrack track) {
		List<TGString> strings = new ArrayList<TGString>();
		for(int i = 0; i < track.getStrings().size(); i++) {
			TGString realString = (TGString)track.getStrings().get(i);
			strings.add(realString.clone(songManager.getFactory()));
		}
		return strings;
	}
	
	public void updateStrings(List<TGString> strings, TGSongManager songManager, int stringCount, boolean percussionChannel) {
		strings.clear();
		if( percussionChannel ) {
			strings.addAll((List<TGString>)songManager.createPercussionStrings(stringCount));
		} else {
			strings.addAll((List<TGString>)songManager.createDefaultInstrumentStrings(stringCount));
		}
	}
	
	public TGSelectableItem[] createSelectableTunings() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		
		int count = (MAX_NOTES * MAX_OCTAVES);
		for (int i = 0; i < count; i++) {
			String noteName = (KEY_NAMES[ (i -  ((i / MAX_NOTES) * MAX_NOTES) ) ] + (i / MAX_NOTES));
			selectableItems.add(new TGSelectableItem(Integer.valueOf(i), noteName));
		}
		
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		
		return builtItems;
	}
	
	public TGSelectableItem[] createSelectableIntegers(int minimum, int maximum) {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		
		for(int value = minimum ; value <= maximum ; value ++) {
			selectableItems.add(new TGSelectableItem(Integer.valueOf(value), Integer.toString(value)));
		}
		
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		
		return builtItems;
	}
	
	public TGSelectableItem[] createSelectableStrings() {
		return this.createSelectableIntegers(MIN_STRINGS, MAX_STRINGS);
	}
	
	public TGSelectableItem[] createSelectableOffsets() {
		return this.createSelectableIntegers(TGTrack.MIN_OFFSET, TGTrack.MAX_OFFSET);
	}
	
	public int findSelectedStrings(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.track_tuning_dlg_strings_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public int findSelectedOffset(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.track_tuning_dlg_offset_value);
		
		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	public List<TGString> findSelectedTunings(View view, TGSongManager songManager) {
		List<TGString> strings = new ArrayList<TGString>();
		
		int stringCount = findSelectedStrings(view);
		for(int i = 0 ; i < TUNING_SPINNER_IDS.length ; i ++ ) {
			if( i < stringCount ) {
				Spinner spinner = (Spinner) view.findViewById(TUNING_SPINNER_IDS[i]);
				TGString tgString = songManager.getFactory().newString();
				tgString.setNumber((i + 1));
				tgString.setValue(((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue());
				strings.add(tgString);
			}
		}
		return strings;
	}
	
	public Boolean findOptionValue(View view, int optionId) {
		return Boolean.valueOf(((CheckBox) view.findViewById(optionId)).isChecked());
	}
	
	public void fillStrings(final View view, final TGSongManager songManager, final TGSong song, final TGTrack track, final List<TGString> strings) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableStrings());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) view.findViewById(R.id.track_tuning_dlg_strings_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(track.stringCount()), null)), false);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				onStringCountChange(view, songManager, song, track, strings);
			}
			
			public void onNothingSelected(AdapterView<?> parentView) {
				onStringCountChange(view, songManager, song, track, strings);
			}
		});
	}
	
	public void fillOffset(View view, TGTrack track) {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableOffsets());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) view.findViewById(R.id.track_tuning_dlg_offset_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(track.getOffset()), null)), false);
	}
	
	public void fillTunings(View view) {
		for(int i = 0 ; i < TUNING_SPINNER_IDS.length ; i ++ ) {
			ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableTunings());
			arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			Spinner spinner = (Spinner) view.findViewById(TUNING_SPINNER_IDS[i]);
			spinner.setAdapter(arrayAdapter);
		}
	}
	
	public void fillOptions(final View view, final TGSongManager songManager, final TGSong song, final TGTrack track) {
		CheckBox stringTransposition = (CheckBox) view.findViewById(R.id.track_tuning_dlg_options_transpose);
		stringTransposition.setChecked(true);
		stringTransposition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onTransposeOptionChanged(view, songManager, song, track);
			}
		});
		((CheckBox) view.findViewById(R.id.track_tuning_dlg_options_transpose_apply_to_chords)).setChecked(true);
		((CheckBox) view.findViewById(R.id.track_tuning_dlg_options_transpose_try_keep_strings)).setChecked(true);
	}
	
	public void updateItems(View view, List<TGString> strings, boolean percussionChannel) {
		this.updateTuningValues(view, strings, !percussionChannel);
		this.updateOffset(view, !percussionChannel);
		this.updateOptions(view, !percussionChannel);
	}
	
	public void updateOffset(View view, boolean enabled) {
		Spinner spinner = (Spinner) view.findViewById(R.id.track_tuning_dlg_offset_value);
		spinner.setEnabled(enabled);
	}
	
	public void updateOptions(View view, boolean enabled) {
		CheckBox stringTransposition = (CheckBox) view.findViewById(R.id.track_tuning_dlg_options_transpose);
		CheckBox stringTranspositionApplyToChords = (CheckBox) view.findViewById(R.id.track_tuning_dlg_options_transpose_apply_to_chords);
		CheckBox stringTranspositionTryKeepString = (CheckBox) view.findViewById(R.id.track_tuning_dlg_options_transpose_try_keep_strings);
		
		boolean stringTranspositionChecked = stringTransposition.isChecked();
		
		stringTransposition.setEnabled(enabled);
		stringTranspositionApplyToChords.setEnabled(enabled && stringTranspositionChecked);
		stringTranspositionTryKeepString.setEnabled(enabled && stringTranspositionChecked);
		if(!stringTranspositionChecked ) {
			stringTranspositionApplyToChords.setChecked(false);
			stringTranspositionTryKeepString.setChecked(false);
		}
	}
	
	public void updateTuningValues(View view, List<TGString> strings, boolean enabled) {
		for (int i = 0; i < strings.size(); i++) {
			TGString string = (TGString) strings.get(i);
			this.updateTuningValues(view, TUNING_SPINNER_IDS[i], string.getValue(), enabled, View.VISIBLE);
		}
		
		for (int i = strings.size(); i < MAX_STRINGS; i++) {
			this.updateTuningValues(view, TUNING_SPINNER_IDS[i], 0, false, View.GONE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateTuningValues(View view, int spinnerId, int value, boolean enabled, int visibility) {
		TGSelectableItem selection = new TGSelectableItem(Integer.valueOf(value), null);
		Spinner spinner = (Spinner) view.findViewById(spinnerId);
		spinner.setSelection(((ArrayAdapter<TGSelectableItem>)spinner.getAdapter()).getPosition(selection), false);
		spinner.setVisibility(visibility);
		spinner.setEnabled(enabled);
	}
	
	public void onStringCountChange(View view, TGSongManager songManager, TGSong song, TGTrack track, List<TGString> strings) {
		int stringCount = findSelectedStrings(view);
		boolean percussionChannel = songManager.isPercussionChannel(song, track.getChannelId());
		
		this.updateStrings(strings, songManager, stringCount, percussionChannel);
		this.updateTuningValues(view, strings, !percussionChannel);
	}
	
	public void onTransposeOptionChanged(View view, TGSongManager songManager, TGSong song, TGTrack track) {
		boolean percussionChannel = songManager.isPercussionChannel(song, track.getChannelId());
		
		this.updateOptions(view, !percussionChannel);
	}
	
	public void updateTrackProperties(View view, TGSongManager songManager, TGTrack track) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTrackPropertiesAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);

		tgActionProcessor.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_INFO, Boolean.TRUE);
		tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, track.getName());
		tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, track.getColor());
		tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, findSelectedOffset(view));

		tgActionProcessor.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_TUNING, Boolean.TRUE);
		tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, findSelectedTunings(view, songManager));
		tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_STRINGS, findOptionValue(view, R.id.track_tuning_dlg_options_transpose));
		tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS, findOptionValue(view, R.id.track_tuning_dlg_options_transpose_apply_to_chords));
		tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS, findOptionValue(view, R.id.track_tuning_dlg_options_transpose_try_keep_strings));
		tgActionProcessor.processOnNewThread();
	}
}
