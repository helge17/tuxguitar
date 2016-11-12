package org.herac.tuxguitar.android.fragment.impl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import org.herac.tuxguitar.android.activity.TGActivity;
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

	private static final String PREFERENCES_NAME = "tuxguitar-settings";

	private Map<String, String> updateActionsMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getPreferenceManager().setSharedPreferencesName(PREFERENCES_NAME);
		this.addPreferencesFromResource(R.xml.preferences_main);
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		this.createUpdateActionsMap();
		this.createOutputPortValues();
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

	public void createOutputPortValues() {
		String defaultValue = null;
		List<String> entryNames = new ArrayList<String>();
		List<String> entryValues = new ArrayList<String>();

		MidiPlayer midiPlayer = MidiPlayer.getInstance(this.findContext());
		List<MidiOutputPort> outputPorts = midiPlayer.listOutputPorts();
		for(MidiOutputPort outputPort : outputPorts) {
			entryNames.add(outputPort.getName());
			entryValues.add(outputPort.getKey());
			if( midiPlayer.isOutputPortOpen(outputPort.getKey()) ) {
				defaultValue = outputPort.getKey();
			}
		}

		ListPreference listPreference = (ListPreference) this.findPreference(TGTransportProperties.PROPERTY_MIDI_OUTPUT_PORT);
		listPreference.setEntries(entryNames.toArray(new CharSequence[entryNames.size()]));
		listPreference.setEntryValues(entryValues.toArray(new CharSequence[entryValues.size()]));
		if( defaultValue != null ) {
			listPreference.setValue(defaultValue);
		}
		listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object o) {
				updatePreferenceSummary(preference, o.toString(), R.string.preferences_midi_output_port_summary, R.string.preferences_midi_output_port_summary_empty);

				return true;
			}
		});
		updatePreferenceSummary(listPreference, defaultValue, R.string.preferences_midi_output_port_summary, R.string.preferences_midi_output_port_summary_empty);
	}

	public void updatePreferenceSummary(Preference preference, String value, Integer summaryId, Integer emptySummaryId) {
		if((value == null || value.isEmpty()) && emptySummaryId != null ) {
			preference.setSummary(this.getActivity().getString(emptySummaryId));
		} else {
			preference.setSummary(this.getActivity().getString(summaryId, value));
		}
	}

	public TGContext findContext() {
		return ((TGActivity) getActivity()).findContext();
	}
}
