package org.herac.tuxguitar.android.fragment.impl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.properties.TGSharedPreferencesUtil;
import org.herac.tuxguitar.android.storage.TGStorageProperties;
import org.herac.tuxguitar.android.transport.TGTransportProperties;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TGPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String MODULE = "tuxguitar";
	public static final String RESOURCE = "settings";

	private Map<String, String> updateActionsMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getPreferenceManager().setSharedPreferencesName(TGSharedPreferencesUtil.getSharedPreferencesName(this.getActivity(), MODULE, RESOURCE));
		this.addPreferencesFromResource(R.xml.preferences_main);
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		this.createUpdateActionsMap();
		this.createSafPreferences();
		this.createOutputPortPreferences();
	}

	@Override
	public void onDestroy() {
		this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if( this.updateActionsMap != null && this.updateActionsMap.containsKey(key) ) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), this.updateActionsMap.get(key));
			tgActionProcessor.process();
		}
	}

	public void createUpdateActionsMap() {
		this.updateActionsMap = new HashMap<String, String>();
		this.updateActionsMap.put(TGTransportProperties.PROPERTY_MIDI_OUTPUT_PORT, TGTransportLoadSettingsAction.NAME);
		this.updateActionsMap.put(TGStorageProperties.PROPERTY_SAF_PROVIDER, TGStorageLoadSettingsAction.NAME);
	}

	public void createSafPreferences() {
		CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.findPreference(TGStorageProperties.PROPERTY_SAF_PROVIDER);
		checkBoxPreference.setChecked(new TGStorageProperties(this.findContext()).isUseSafProvider());
	}

	public void createOutputPortPreferences() {
		String currentValue = null;
		String currentLabel = null;
		final List<String> entryNames = new ArrayList<String>();
		final List<String> entryValues = new ArrayList<String>();

		MidiPlayer midiPlayer = MidiPlayer.getInstance(this.findContext());
		List<MidiOutputPort> outputPorts = midiPlayer.listOutputPorts();
		for(MidiOutputPort outputPort : outputPorts) {
			entryNames.add(outputPort.getName());
			entryValues.add(outputPort.getKey());
			if( midiPlayer.isOutputPortOpen(outputPort.getKey()) ) {
				currentValue = outputPort.getKey();
				currentLabel = outputPort.getName();
			}
		}

		final ListPreference listPreference = (ListPreference) this.findPreference(TGTransportProperties.PROPERTY_MIDI_OUTPUT_PORT);
		listPreference.setEntries(entryNames.toArray(new CharSequence[entryNames.size()]));
		listPreference.setEntryValues(entryValues.toArray(new CharSequence[entryValues.size()]));
		if( currentValue != null ) {
			listPreference.setValue(currentValue);
		}
		listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object o) {
				int index = ( o != null ? entryValues.indexOf(o.toString()) : -1);
				String selectedLabel = ( index >= 0 ? entryNames.get(index) : null);

				updatePreferenceSummary(preference, selectedLabel, R.string.preferences_midi_output_port_summary, R.string.preferences_midi_output_port_summary_empty);

				return true;
			}
		});
		updatePreferenceSummary(listPreference, currentLabel, R.string.preferences_midi_output_port_summary, R.string.preferences_midi_output_port_summary_empty);
	}

	public void updatePreferenceSummary(Preference preference, String label, Integer summaryId, Integer emptySummaryId) {
		if( label != null && !label.isEmpty() ) {
			preference.setSummary(this.getActivity().getString(summaryId, label));
		} else if (emptySummaryId != null) {
			preference.setSummary(this.getActivity().getString(emptySummaryId));
		}
	}

	public TGContext findContext() {
		return ((TGActivity) getActivity()).findContext();
	}
}
