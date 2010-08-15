package org.herac.tuxguitar.io.gpx.score;

public class GPXVoice {
	
	private int id;
	private int[] beatIds;
	
	public GPXVoice(){
		super();
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int[] getBeatIds() {
		return this.beatIds;
	}
	
	public void setBeatIds(int[] beatIds) {
		this.beatIds = beatIds;
	}
}
