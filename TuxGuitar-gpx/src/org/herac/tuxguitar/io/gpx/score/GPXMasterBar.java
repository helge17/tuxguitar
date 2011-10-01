package org.herac.tuxguitar.io.gpx.score;

public class GPXMasterBar {
	
	private int[] barIds;
	private int[] time;
	
	private int repeatCount;
	private boolean repeatStart;
	private int accidentalCount;
	private String mode;
	private String tripletFeel;
	
	public GPXMasterBar(){
		this.accidentalCount = 0;
		this.mode = null;
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

	public int getAccidentalCount() {
		return accidentalCount;
	}

	public void setAccidentalCount(int accidentalCount) {
		this.accidentalCount = accidentalCount;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTripletFeel() {
		return this.tripletFeel;
	}
	
	public void setTripletFeel(String tripletFeel) {
		this.tripletFeel = tripletFeel;
	}
}
