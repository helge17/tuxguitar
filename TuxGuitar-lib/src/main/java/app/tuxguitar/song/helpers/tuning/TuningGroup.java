package app.tuxguitar.song.helpers.tuning;

import java.util.ArrayList;
import java.util.List;

public class TuningGroup {

	private String name;
	private List<TuningPreset> tunings;
	private List<TuningGroup> groups;
	private TuningGroup parent;

	public TuningGroup(TuningGroup parent, String name, List<TuningPreset> tunings, List<TuningGroup> groups) {
		this.parent = parent;
		this.name = name;
		this.tunings = tunings;
		this.groups = groups;
	}
	public TuningGroup(TuningGroup parent, String name) {
		this(parent, name, new ArrayList<TuningPreset>(), new ArrayList<TuningGroup>());
	}
	public TuningGroup() {
		this(null, "");
	}

	public TuningGroup getParent() {
		return this.parent;
	}

	public String getName() {
		return this.name;
	}

	public List<TuningPreset> getTunings() {
		return this.tunings;
	}

	public List<TuningGroup> getGroups() {
		return this.groups;
	}

	public void addGroup(TuningGroup group) {
		this.groups.add(group);
		group.parent = this;
	}

	public void addTuningPreset(TuningPreset preset) {
		preset.setParent(this);
		this.tunings.add(preset);
	}

	public void removeTuningPreset(TuningPreset preset) {
		this.tunings.remove(preset);
	}

	public void setName(String name) {
		this.name = name;
	}

}
