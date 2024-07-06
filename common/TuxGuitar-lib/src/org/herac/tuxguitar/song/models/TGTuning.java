package org.herac.tuxguitar.song.models;

import java.util.Arrays;

public class TGTuning implements Comparable<TGTuning> {

	private String name;
	private int[] values;
	private Integer priority = null;

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
	
	public void setPriority(int prio) {
		this.priority = prio;
	}
	
	public Integer getPriority() {
		return this.priority;
	}

	public boolean isWithinRange(int minVal, int maxVal) {
		if (values.length == 0)
			return false;
		
		int[] tmp = values.clone();
		// sort note value from low to high
		Arrays.sort(tmp);
		
		return (minVal >= tmp[0]) && (maxVal <= tmp[values.length - 1]);

	}
	
	@Override
	public int compareTo(TGTuning other) {
		if (this.priority == null && other.getPriority() == null)
			return 0;
		else if (this.priority != null && other.getPriority() == null)
			return -1;
		else if (this.priority == null && other.getPriority() != null)
			return 1 ;
		
		return this.priority.intValue() - other.getPriority().intValue();
	}
}
