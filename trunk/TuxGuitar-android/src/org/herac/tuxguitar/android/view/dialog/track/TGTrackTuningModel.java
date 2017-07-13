package org.herac.tuxguitar.android.view.dialog.track;

public class TGTrackTuningModel {

	private Integer value;
	
	public TGTrackTuningModel() {
		super();
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getName() {
		return TGTrackTuningLabel.valueOf(this.getValue());
	}
}
