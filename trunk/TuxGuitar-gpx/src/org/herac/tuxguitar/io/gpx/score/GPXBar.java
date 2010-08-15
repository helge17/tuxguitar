package org.herac.tuxguitar.io.gpx.score;

public class GPXBar {
	
	private int id;
	private int[] voiceIds;
	private String clef;
	private String simileMark;
	
	public GPXBar(){
		super();
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int[] getVoiceIds() {
		return this.voiceIds;
	}
	
	public void setVoiceIds(int[] voiceIds) {
		this.voiceIds = voiceIds;
	}
	
	public String getClef() {
		return this.clef;
	}
	
	public void setClef(String clef) {
		this.clef = clef;
	}

	public String getSimileMark() {
		return simileMark;
	}

	public void setSimileMark(String simileMark) {
		this.simileMark = simileMark;
	}
}
