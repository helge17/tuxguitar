package org.herac.tuxguitar.song.helpers.tuning;

import org.herac.tuxguitar.song.models.TGTuning;

public class TuningPreset extends TGTuning {
	private TuningGroup parent;
	
	public TuningPreset(TuningGroup parent, String name, int[] values) {
		this.parent = parent;
		this.setName(name);
		this.setValues(values);
	}
	
	public TuningGroup getParent() {
		return this.parent;
	}
	
	public void setParent(TuningGroup group) {
		this.parent = group;
	}
}
