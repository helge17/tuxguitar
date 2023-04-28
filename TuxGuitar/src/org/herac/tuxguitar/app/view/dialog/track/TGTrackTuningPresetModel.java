package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.song.helpers.tuning.TuningGroup;

public class TGTrackTuningPresetModel {

	private String name;
	private TGTrackTuningModel[] values;
	private TGTrackTuningGroupEntryModel entry;

	public TGTrackTuningPresetModel() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public TGTrackTuningModel[] getValues() {
		return this.values;
	}

	public TGTrackTuningGroupEntryModel getEntry() { return entry; }

	public void setValues(TGTrackTuningModel[] values) {
		this.values = values;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEntry(TGTrackTuningGroupEntryModel entry) { this.entry = entry; }
}
