package org.herac.tuxguitar.android.fragment.impl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.transport.TGTransportProperties;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class TGTransportPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String PREFERENCES_NAME = (TGTransportProperties.MODULE + "-" + TGTransportProperties.RESOURCE);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getPreferenceManager().setSharedPreferencesName(PREFERENCES_NAME);
		this.addPreferencesFromResource(R.xml.preferences_transport);
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		this.createOutputPortValues();
	}

	@Override
	public void onDestroy() {
		this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), TGTransportLoadSettingsAction.NAME);
		tgActionProcessor.process();
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
		listPreference.setDefaultValue(defaultValue);
	}

	public TGContext findContext() {
		return ((TGActivity) getActivity()).findContext();
	}
}
