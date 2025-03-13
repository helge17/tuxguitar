package org.herac.tuxguitar.android.view.dialog.track;

public class TGTrackTuningPresetModel {

	private String name;
	private TGTrackTuningModel[] values;

	public TGTrackTuningPresetModel() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public TGTrackTuningModel[] getValues() {
		return this.values;
	}

	public void setValues(TGTrackTuningModel[] values) {
		this.values = values;
	}

	public void setName(String name) {
		this.name = name;
	}
}
