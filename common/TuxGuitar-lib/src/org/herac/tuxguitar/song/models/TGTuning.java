package org.herac.tuxguitar.song.models;

public class TGTuning implements Comparable<TGTuning>{

	private String name;
	private int[] values;
  private Integer priority = null; 

	public void setName(String name) {
		this.name = name;
	}

	public void setValues(int[] values) {
		this.values = values;
	}

  public void setPriority(int prio) {
    this.priority = prio;
  }

	public String getName() {
		return this.name;
	}
	public int[] getValues() {
		return this.values;
	}

  public Integer getPriority() { 
    return this.priority;
  }

  @Override
  public int compareTo(TGTuning other) {
    if (this.priority == null && other.getPriority() == null)
      return 0;
    else if (this.priority != null && other.getPriority() == null)
      return -1;
    else if (this.priority == null && other.getPriority() != null)
      return 1;

    return this.priority.intValue() - other.getPriority().intValue();
  }
}
