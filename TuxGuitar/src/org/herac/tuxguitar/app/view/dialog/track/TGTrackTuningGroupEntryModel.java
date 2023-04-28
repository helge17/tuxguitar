package org.herac.tuxguitar.app.view.dialog.track;

public class TGTrackTuningGroupEntryModel {

	private TGTrackTuningPresetModel preset;
	private TGTrackTuningGroupModel group;
	private TGTrackTuningGroupModel parent;

	public TGTrackTuningPresetModel getPreset() {
		return preset;
	}
	public TGTrackTuningGroupModel getGroup() {
		return group;
	}
	public TGTrackTuningGroupModel getParent() {
		return parent;
	}

	public void setPreset(TGTrackTuningPresetModel preset) {
		this.preset = preset;
		this.group = null;
	}
	public void setGroup(TGTrackTuningGroupModel group) {
		this.group = group;
		this.preset = null;
	}
	public void setParent(TGTrackTuningGroupModel parent) {
		this.parent = parent;
	}

}
