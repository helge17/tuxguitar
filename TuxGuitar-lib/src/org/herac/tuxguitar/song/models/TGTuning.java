package org.herac.tuxguitar.song.models;

public class TGTuning {

	private String name;
	private int[] values;

	public void setName(String name) {
		this.name = name;
	}

	public void setValues(int[] values) {
		this.values = values;
	}
	public String getName() {
		return this.name;
	}
	public int[] getValues() {
		return this.values;
	}
}
