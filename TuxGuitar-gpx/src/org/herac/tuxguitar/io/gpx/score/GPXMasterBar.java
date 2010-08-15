package org.herac.tuxguitar.io.gpx.score;

public class GPXMasterBar {
	
	private int[] barIds;
	private int[] time;
	
	private int repeatCount;
	private boolean repeatStart;
	
	public GPXMasterBar(){
		super();
	}
	
	public int[] getBarIds() {
		return this.barIds;
	}
	
	public void setBarIds(int[] barIds) {
		this.barIds = barIds;
	}
	
	public int[] getTime() {
		return time;
	}
	
	public void setTime(int[] time) {
		this.time = time;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public boolean isRepeatStart() {
		return repeatStart;
	}

	public void setRepeatStart(boolean repeatStart) {
		this.repeatStart = repeatStart;
	}
}
